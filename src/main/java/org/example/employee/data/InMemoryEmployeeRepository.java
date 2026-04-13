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
 * Simple in-memory implementation of EmployeeRepository used for tests and runtime.
 * Stores employees in a map and supports basic queries and statistics.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> employeeMap = new HashMap<>();
    private Long currentMaxId = 0L;

    @Override
    public Long getNextId() {
        currentMaxId++;
        return currentMaxId;
    }

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

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMap.get(id);
    }

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

    @Override
    public Collection<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

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

    @Override
    public List<Employee> getEmployeesByTypeSorted(String type) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employeeMap.values()) {
            if (emp.getGroupId().equalsIgnoreCase(type)) filtered.add(emp);
        }
        filtered.sort(null);
        return filtered;
    }

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
        }
        return topEmployee;
    }

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
