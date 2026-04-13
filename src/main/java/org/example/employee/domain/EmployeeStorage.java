package org.example.employee.domain;

import org.example.employee.domain.model.Employee;
import org.example.employee.domain.exceptions.StorageException;

import java.util.Collection;
import java.util.List;

/**
 * Abstraction over persistent storage for employees.
 * Implementations are responsible for saving and loading all employees.
 */
public interface EmployeeStorage {
    /**
     * Save a collection of employees to the underlying storage.
     *
     * @param employees collection of employee objects to persist
     * @throws StorageException when an I/O or storage-specific error occurs
     */
    void saveAll(Collection<Employee> employees) throws StorageException;

    /**
     * Load all employees from the underlying storage.
     *
     * @return list of employees loaded from storage
     * @throws StorageException when an I/O or storage-specific error occurs
     */
    List<Employee> loadAll() throws StorageException;
}
