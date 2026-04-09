package org.example.employee.domain;

import java.util.List;
import java.util.Map;

public interface EmployeeRepository {
    Long getNextId();
    void addEmployee(Employee employee);
    Employee getEmployeeById(Long id);
    void removeEmployeeById(Long id);
    List<Employee> getAllEmployees();

   void saveToFile(String filename);

    void loadFromFile(String filename);

    List<Employee> getEmployeesByTypeSorted(String type);

    String getMostFrequentCollaborationLevel();

    Employee getEmployeeWithMostConnections();

    Map<String, Integer> getEmployeeCountsByGroup();

}
