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

public class SqliteEmployeeStorage implements EmployeeStorage {

    private final String dbUrl;

    public SqliteEmployeeStorage(String dbFileName) {
        this.dbUrl = "jdbc:sqlite:" + dbFileName;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement stmt = conn.createStatement()) {

            // Create employees table
            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY," +
                    "type TEXT NOT NULL," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "birth_year INTEGER NOT NULL" +
                    ");";
            stmt.execute(createEmployeesTable);

            // Create coworkers table
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
            // Since this happens in the constructor, we might not want to throw a checked
            // exception here,
            // but the application should know if the DB can't be created.
            // For now, logging to stderr is a basic fallback.
        }
    }

    @Override
    public void saveAll(Collection<Employee> employees) throws StorageException {
        String insertEmployeeSql = "INSERT INTO employees(id, type, first_name, last_name, birth_year) VALUES(?,?,?,?,?) "
                +
                "ON CONFLICT(id) DO UPDATE SET type=excluded.type, first_name=excluded.first_name, last_name=excluded.last_name, birth_year=excluded.birth_year;";
        String insertCoworkerSql = "INSERT INTO coworkers(employee_id, coworker_id, collaboration_level) VALUES(?,?,?) "
                +
                "ON CONFLICT(employee_id, coworker_id) DO UPDATE SET collaboration_level=excluded.collaboration_level;";

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            // Use transaction
            conn.setAutoCommit(false);

            try (PreparedStatement empStmt = conn.prepareStatement(insertEmployeeSql);
                    PreparedStatement cwStmt = conn.prepareStatement(insertCoworkerSql);
                    Statement clearCwStmt = conn.createStatement()) {

                // Clear existing coworkers to handle removed collaborations easily
                clearCwStmt.execute("DELETE FROM coworkers");

                for (Employee emp : employees) {
                    empStmt.setLong(1, emp.getId());
                    empStmt.setString(2, emp.getGroupId());
                    empStmt.setString(3, emp.getFirstName());
                    empStmt.setString(4, emp.getLastName());
                    empStmt.setInt(5, emp.getBirthYear());
                    empStmt.executeUpdate();

                    for (var entry : emp.getCoworkers().entrySet()) {
                        cwStmt.setLong(1, emp.getId());
                        cwStmt.setLong(2, entry.getKey());
                        cwStmt.setString(3, entry.getValue().name());
                        cwStmt.executeUpdate();
                    }
                }

                // Remove employees that are no longer in the collection
                if (!employees.isEmpty()) {
                    StringBuilder ids = new StringBuilder();
                    for (Employee emp : employees) {
                        ids.append(emp.getId()).append(",");
                    }
                    ids.deleteCharAt(ids.length() - 1); // remove last comma
                    try (Statement delStmt = conn.createStatement()) {
                        delStmt.execute("DELETE FROM employees WHERE id NOT IN (" + ids.toString() + ")");
                    }
                } else {
                    try (Statement delStmt = conn.createStatement()) {
                        delStmt.execute("DELETE FROM employees");
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new StorageException(StorageError.PERSISTENCE_ERROR);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new StorageException(StorageError.PERSISTENCE_ERROR);
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
