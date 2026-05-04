package org.example.employee.domain;

import org.example.employee.domain.model.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface EmployeeRepository {

    Long getNextId();


    void addEmployee(Employee employee);


    Employee getEmployeeById(Long id);


    void removeEmployeeById(Long id);


    Collection<Employee> getAllEmployees();


    void loadAll(List<Employee> employees);


    List<Employee> getEmployeesByTypeSorted(String type);


    String getMostFrequentCollaborationLevel();


    Employee getEmployeeWithMostConnections();


    Map<String, Integer> getEmployeeCountsByGroup();
}
