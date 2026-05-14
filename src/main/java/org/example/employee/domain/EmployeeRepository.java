package org.example.employee.domain;

import org.example.employee.domain.model.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Interface defining the standard operations for an employee repository.
 * Handles the storage, retrieval, and analysis of {@link Employee} data.
 */
public interface EmployeeRepository {

    /**
     * Generates the next unique identifier for a new employee.
     *
     * @return The generated ID.
     */
    Long getNextId();


    /**
     * Adds a new employee to the repository.
     *
     * @param employee The employee to add.
     */
    void addEmployee(Employee employee);


    /**
     * Retrieves an employee by their ID.
     *
     * @param id The ID to look up.
     * @return The employee, or null if not found.
     */
    Employee getEmployeeById(Long id);


    /**
     * Removes an employee from the repository by their ID.
     *
     * @param id The ID of the employee to remove.
     */
    void removeEmployeeById(Long id);


    /**
     * Retrieves all employees currently stored.
     *
     * @return A collection of all employees.
     */
    Collection<Employee> getAllEmployees();


    /**
     * Clears current data and loads the provided list of employees.
     *
     * @param employees The list to load.
     */
    void loadAll(List<Employee> employees);


    /**
     * Returns a sorted list of employees that belong to the specified group type.
     *
     * @param type The group ID.
     * @return A sorted list of employees.
     */
    List<Employee> getEmployeesByTypeSorted(String type);


    /**
     * Returns a description of the most frequently occurring collaboration level across all employees.
     *
     * @return A string with the level and its count.
     */
    String getMostFrequentCollaborationLevel();


    /**
     * Returns the employee who has the most active collaboration connections.
     *
     * @return The most connected employee.
     */
    Employee getEmployeeWithMostConnections();


    /**
     * Returns the counts of employees grouped by their group type.
     *
     * @return A map where keys are group types and values are counts.
     */
    Map<String, Integer> getEmployeeCountsByGroup();
}
