package org.example.employee.domain;

import org.example.employee.domain.model.Employee;
import org.example.employee.domain.exceptions.StorageException;

import java.util.Collection;
import java.util.List;


public interface EmployeeStorage {

    void saveAll(Collection<Employee> employees) throws StorageException;


    List<Employee> loadAll() throws StorageException;
}
