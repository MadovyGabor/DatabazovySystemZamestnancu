package org.example.employee.domain;

import org.example.employee.domain.model.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * In-memory or persistent repository API for managing Employee entities.
 * Responsible for CRUD-ish operations and aggregate queries used by services.
 */
public interface EmployeeRepository {
    /**
     * Return the next available numeric id for a new employee.
     */
    Long getNextId();

    /**
     * Persist a new employee into the repository.
     */
    void addEmployee(Employee employee);

    /**
     * Retrieve an employee by id or null if not found.
     */
    Employee getEmployeeById(Long id);

    /**
     * Remove an employee (and any relations maintained by the repository implementation) by id.
     */
    void removeEmployeeById(Long id);

    /**
     * Return all employees present in the repository.
     */
    Collection<Employee> getAllEmployees();

    /**
     * Bulk load a list of employees into the repository (replace current contents).
     */
    void loadAll(List<Employee> employees);

    /**
     * Return employees filtered by group id and sorted (natural ordering by name).
     */
    List<Employee> getEmployeesByTypeSorted(String type);

    /**
     * Return a human-readable description of the most frequent collaboration level.
     */
    String getMostFrequentCollaborationLevel();

    /**
     * Return the employee who has the most coworker connections or null if none.
     */
    Employee getEmployeeWithMostConnections();

    /**
     * Return counts of employees grouped by their group id (e.g. DA, SS).
     */
    Map<String, Integer> getEmployeeCountsByGroup();
}
