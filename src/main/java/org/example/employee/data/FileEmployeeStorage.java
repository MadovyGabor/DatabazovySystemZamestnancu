package org.example.employee.data;

import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.EmployeeStorage;
import org.example.employee.domain.model.employeeType.DataAnalyst;
import org.example.employee.domain.model.employeeType.SecuritySpecialist;
import org.example.employee.domain.exceptions.StorageError;
import org.example.employee.domain.exceptions.StorageException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileEmployeeStorage implements EmployeeStorage {

    private final File file;

    public FileEmployeeStorage(String filename) {
        this.file = new File(filename);
    }

    @Override
    public void saveAll(Collection<Employee> employees) throws StorageException {
        if (file.exists() && !file.canWrite()) {
            throw new StorageException(StorageError.FILE_ACCESS_DENIED);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Employee emp : employees) {
                StringBuilder sb = new StringBuilder();
                sb.append(emp.getId()).append(";")
                        .append(emp.getGroupId()).append(";")
                        .append(emp.getFirstName()).append(";")
                        .append(emp.getLastName()).append(";")
                        .append(emp.getBirthYear()).append(";");

                List<String> coworkersList = new ArrayList<>();
                emp.getCoworkers().forEach((coworkerId, level) -> coworkersList.add(coworkerId + "=" + level.name()));
                sb.append(String.join(",", coworkersList));

                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException(StorageError.FILE_WRITE_ERROR);
        }
    }

    @Override
    public List<Employee> loadAll() throws StorageException {
        if (!file.exists()) {
            throw new StorageException(StorageError.FILE_NOT_FOUND);
        }
        if (!file.canRead()) {
            throw new StorageException(StorageError.FILE_ACCESS_DENIED);
        }

        List<Employee> employees = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 6) {
                    throw new StorageException(StorageError.DATA_CORRUPTED, String.valueOf(lineNumber));
                }

                Long id = Long.parseLong(parts[0]);
                String type = parts[1];
                String firstName = parts[2];
                String lastName = parts[3];
                int birthYear = Integer.parseInt(parts[4]);


                Employee emp = switch (type) {
                    case "DA" -> new DataAnalyst(id, firstName, lastName, birthYear);
                    case "SS" -> new SecuritySpecialist(id, firstName, lastName, birthYear);
                    default -> throw new StorageException(StorageError.DATA_CORRUPTED, String.valueOf(lineNumber));
                };

                String coworkersString = parts[5];
                if (!coworkersString.isEmpty()) {
                    String[] cwPairs = coworkersString.split(",");
                    for (String pair : cwPairs) {
                        String[] kv = pair.split("=");
                        if (kv.length != 2) {
                            throw new StorageException(StorageError.DATA_CORRUPTED, String.valueOf(lineNumber));
                        }
                        Long cwId = Long.parseLong(kv[0]);
                        CollaborationLevel level = CollaborationLevel.valueOf(kv[1]);
                        emp.addCoworker(cwId, level);
                    }

                }
                employees.add(emp);
            }
            return employees;
        } catch (IllegalArgumentException e) {
            throw new StorageException(StorageError.DATA_CORRUPTED, String.valueOf(lineNumber));
        } catch (IOException e) {
            throw new StorageException(StorageError.FILE_READ_ERROR);
        }
    }
}

