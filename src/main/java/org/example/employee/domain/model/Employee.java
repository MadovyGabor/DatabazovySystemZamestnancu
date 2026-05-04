package org.example.employee.domain.model;


import org.example.employee.domain.EmployeeRepository;

import java.util.HashMap;
import java.util.Map;


public abstract class Employee implements Comparable<Employee>{

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int birthYear;

    private Map<Long, CollaborationLevel> coworkers;
    public Employee(Long id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.coworkers = new HashMap<>();
    }


    public Long getId() {
        return id;
    }


    public abstract String getGroupName();


    public abstract String getGroupId();

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }


    public Map<Long, CollaborationLevel> getCoworkers() {
        return coworkers;
    }

    public void addCoworker(Long coworkerId, CollaborationLevel level) {
        this.coworkers.put(coworkerId, level);
    }

    public void removeCoworker(Long coworkerId) {
        this.coworkers.remove(coworkerId);
    }


    public abstract EmployeeTaskResults executeSkill(EmployeeRepository repository);

    @Override
    public int compareTo(Employee other) {
        int res = this.lastName.compareToIgnoreCase(other.lastName);
        if (res == 0) {
            return this.firstName.compareToIgnoreCase(other.firstName);
        }
        return res;
    }
}