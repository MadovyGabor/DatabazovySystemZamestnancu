package org.example.employee.data;

import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An in-memory implementation of the {@link EmployeeRepository} interface.
 * Stores employee data in a HashMap for fast access during runtime.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> employeeMap = new HashMap<>();
    private Long currentMaxId = 0L;

    /**
     * Generates and returns the next available unique ID for a new employee.
     *
     * @return The next ID.
     */
    @Override
    public Long getNextId() {
        currentMaxId++;
        return currentMaxId;
    }

    /**
     * Adds a new employee to the repository.
     *
     * @param employee The employee to add.
     * @throws BusinessException If an employee with the same ID already exists.
     */
    @Override
    public void addEmployee(Employee employee) {
        if (employeeMap.containsKey(employee.getId())) {
            throw new BusinessException(BusinessError.DUPLICATE_ID);
        }
        employeeMap.put(employee.getId(), employee);
        if (employee.getId() > currentMaxId) {
            currentMaxId = employee.getId();
        }
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return The employee object, or null if not found.
     */
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMap.get(id);
    }

    /**
     * Removes an employee from the repository by their ID and also removes them
     * from their coworkers' connections.
     *
     * @param id The ID of the employee to remove.
     * @throws BusinessException If the employee is not found.
     */
    @Override
    public void removeEmployeeById(Long id) {
        Employee removed = employeeMap.get(id);
        if (removed == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }

        for (Long coworkerId : removed.getCoworkers().keySet()) {
            Employee coworker = employeeMap.get(coworkerId);
            if (coworker != null) {
                coworker.removeCoworker(id);
            }
        }

        employeeMap.remove(id);
    }

    /**
     * Retrieves all employees currently stored in the repository.
     *
     * @return A collection of all employees.
     */
    @Override
    public Collection<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    /**
     * Replaces the current repository data with the provided list of employees.
     * Recalculates the current maximum ID.
     *
     * @param employees The list of employees to load.
     */
    @Override
    public void loadAll(List<Employee> employees) {
        employeeMap.clear();
        currentMaxId = 0L;
        for (Employee employee : employees) {
            employeeMap.put(employee.getId(), employee);
            if (employee.getId() > currentMaxId) {
                currentMaxId = employee.getId();
            }
        }
    }

    /**
     * Retrieves all employees of a specific group type, sorted by natural ordering (last name).
     *
     * @param type The group ID (e.g., "DA" or "SS") to filter by.
     * @return A sorted list of employees of the specified type.
     */
    @Override
    public List<Employee> getEmployeesByTypeSorted(String type) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employeeMap.values()) {
            if (emp.getGroupId().equalsIgnoreCase(type)) filtered.add(emp);
        }
        filtered.sort(null);
        return filtered;
    }

    /**
     * Analyzes all collaboration relationships and returns the most frequently occurring level.
     *
     * @return A string representation of the most frequent level and its count.
     */
    @Override
    public String getMostFrequentCollaborationLevel() {
        Map<CollaborationLevel, Integer> stats = new HashMap<>();
        for (Employee emp : employeeMap.values()) {
            for (CollaborationLevel level : emp.getCoworkers().values()) {
                stats.put(level, stats.getOrDefault(level, 0) + 1);
            }
        }

        if (stats.isEmpty()) {
            return "Zadne data";
        }

        CollaborationLevel mostFrequent = null;
        int maxCount = -1;
        for (Map.Entry<CollaborationLevel, Integer> entry : stats.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        return mostFrequent.name() + " (" + maxCount + "x)";
    }

    /**
     * Finds and returns the employee who has the highest number of direct connections.
     * If there's a tie, returns the one with the higher ID.
     *
     * @return The employee with the most connections, or null if repository is empty.
     */
    @Override
    public Employee getEmployeeWithMostConnections() {
        Employee topEmployee = null;
        int maxConnections = -1;
        for (Employee emp : employeeMap.values()) {
            int currentConnections = emp.getCoworkers().size();
            if (currentConnections > maxConnections) {
                maxConnections = currentConnections;
                topEmployee = emp;
            }
            else if (currentConnections == maxConnections) {
                if (emp.getId() > topEmployee.getId()) {
                    topEmployee = emp;
                }
            }
        }

        return topEmployee;
    }

    /**
     * Counts the number of employees in each specific group.
     *
     * @return A map where the key is the group ID and the value is the count.
     */
    @Override
    public Map<String, Integer> getEmployeeCountsByGroup() {
        Map<String, Integer> counts = new HashMap<>();
        for (Employee emp : employeeMap.values()) {
            String groupId = emp.getGroupId();
            counts.put(groupId, counts.getOrDefault(groupId, 0) + 1);
        }
        return counts;
    }
}
