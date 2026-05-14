package org.example.employee.domain;

import org.example.employee.domain.model.Employee;
import org.example.employee.domain.exceptions.StorageException;

import java.util.Collection;
import java.util.List;


/**
 * Interface defining the standard operations for an employee persistent storage.
 */
public interface EmployeeStorage {

    /**
     * Saves a collection of employees to the storage mechanism.
     *
     * @param employees The collection of employees to save.
     * @throws StorageException If an error occurs during saving.
     */
    void saveAll(Collection<Employee> employees) throws StorageException;


    /**
     * Loads a list of employees from the storage mechanism.
     *
     * @return A list of loaded employees.
     * @throws StorageException If an error occurs during loading.
     */
    List<Employee> loadAll() throws StorageException;
}
