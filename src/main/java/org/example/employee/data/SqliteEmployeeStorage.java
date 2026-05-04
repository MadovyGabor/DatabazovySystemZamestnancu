package org.example.employee.data;

import org.example.employee.domain.EmployeeStorage;
import org.example.employee.domain.exceptions.StorageError;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SqliteEmployeeStorage implements EmployeeStorage {

    private final String dbUrl;

    public SqliteEmployeeStorage(String dbFileName) {
        this.dbUrl = "jdbc:sqlite:" + dbFileName;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement stmt = conn.createStatement()) {


            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY," +
                    "type TEXT NOT NULL," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "birth_year INTEGER NOT NULL" +
                    ");";
            stmt.execute(createEmployeesTable);


            String createCoworkersTable = "CREATE TABLE IF NOT EXISTS coworkers (" +
                    "employee_id INTEGER NOT NULL," +
                    "coworker_id INTEGER NOT NULL," +
                    "collaboration_level TEXT NOT NULL," +
                    "PRIMARY KEY (employee_id, coworker_id)," +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createCoworkersTable);

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    @Override
    public void saveAll(Collection<Employee> employees) throws StorageException {

        String insertEmployeeSql = "INSERT INTO employees(id, type, first_name, last_name, birth_year) VALUES(?,?,?,?,?) " +
                "ON CONFLICT(id) DO UPDATE SET type=excluded.type, first_name=excluded.first_name, " +
                "last_name=excluded.last_name, birth_year=excluded.birth_year;";

        String insertCoworkerSql = "INSERT INTO coworkers(employee_id, coworker_id, collaboration_level) VALUES(?,?,?) " +
                "ON CONFLICT(employee_id, coworker_id) DO UPDATE SET collaboration_level=excluded.collaboration_level;";

        try (Connection conn = DriverManager.getConnection(dbUrl)) {

            conn.setAutoCommit(false);


            if (employees.isEmpty()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DELETE FROM coworkers");
                    stmt.execute("DELETE FROM employees");
                }
                conn.commit();
                return;
            }


            String ids = employees.stream()
                    .map(e -> String.valueOf(e.getId()))
                    .collect(Collectors.joining(","));

            try (PreparedStatement empStmt = conn.prepareStatement(insertEmployeeSql);
                 PreparedStatement cwStmt = conn.prepareStatement(insertCoworkerSql);
                 Statement syncStmt = conn.createStatement()) {



                syncStmt.execute("DELETE FROM coworkers WHERE employee_id IN (" + ids + ")");


                for (Employee emp : employees) {

                    empStmt.setLong(1, emp.getId());
                    empStmt.setString(2, emp.getGroupId());
                    empStmt.setString(3, emp.getFirstName());
                    empStmt.setString(4, emp.getLastName());
                    empStmt.setInt(5, emp.getBirthYear());
                    empStmt.addBatch();


                    for (var entry : emp.getCoworkers().entrySet()) {
                        cwStmt.setLong(1, emp.getId());
                        cwStmt.setLong(2, entry.getKey());
                        cwStmt.setString(3, entry.getValue().name());
                        cwStmt.addBatch();
                    }
                }


                empStmt.executeBatch();
                cwStmt.executeBatch();


                syncStmt.execute("DELETE FROM employees WHERE id NOT IN (" + ids + ")");


                conn.commit();
            } catch (SQLException e) {

                conn.rollback();
                throw new StorageException(StorageError.PERSISTENCE_ERROR, e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new StorageException(StorageError.PERSISTENCE_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Employee> loadAll() throws StorageException {
        List<Employee> employees = new ArrayList<>();
        String selectEmployees = "SELECT id, type, first_name, last_name, birth_year FROM employees;";
        String selectCoworkers = "SELECT coworker_id, collaboration_level FROM coworkers WHERE employee_id = ?;";

        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement empStmt = conn.createStatement();
                ResultSet empRs = empStmt.executeQuery(selectEmployees);
                PreparedStatement cwStmt = conn.prepareStatement(selectCoworkers)) {

            while (empRs.next()) {
                long id = empRs.getLong("id");
                String type = empRs.getString("type");
                String firstName = empRs.getString("first_name");
                String lastName = empRs.getString("last_name");
                int birthYear = empRs.getInt("birth_year");

                Employee emp = switch (type) {
                    case "DA" -> new DataAnalyst(id, firstName, lastName, birthYear);
                    case "SS" -> new SecuritySpecialist(id, firstName, lastName, birthYear);
                    default -> throw new StorageException(StorageError.DATA_CORRUPTED, "Unknown type: " + type);
                };

                cwStmt.setLong(1, id);
                try (ResultSet cwRs = cwStmt.executeQuery()) {
                    while (cwRs.next()) {
                        long coworkerId = cwRs.getLong("coworker_id");
                        String levelStr = cwRs.getString("collaboration_level");
                        CollaborationLevel level = CollaborationLevel.valueOf(levelStr);
                        emp.addCoworker(coworkerId, level);
                    }
                }
                employees.add(emp);
            }

        } catch (SQLException | IllegalArgumentException e) {
            throw new StorageException(StorageError.PERSISTENCE_ERROR);
        }

        return employees;
    }
}
