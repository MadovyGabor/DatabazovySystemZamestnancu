package org.example.employee.domain.model;


import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract class for all employee types in the system.
 * Provides common identity and personal information and stores collaboration links
 * to other employees using a map of coworkerId -> CollaborationLevel.
 * Concrete subclasses must implement group identification and a work skill action.
 */
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

    /**
     * Returns the unique identifier of the employee.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user-friendly name of the group (e.g. "Data Analyst").
     */
    public abstract String getGroupName();

    /**
     * Returns the short group id used in storage (e.g. "DA" or "SS").
     */
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

    /**
     * Returns a mutable view of coworker relations: coworkerId -> CollaborationLevel.
     */
    public Map<Long, CollaborationLevel> getCoworkers() {
        return coworkers;
    }
    /**
     * Adds a coworker relation with a given collaboration level.
     */
    public void addCoworker(Long coworkerId, CollaborationLevel level) {
        this.coworkers.put(coworkerId, level);
    }
    /**
     * Removes a coworker relation by id.
     */
    public void removeCoworker(Long coworkerId) {
        this.coworkers.remove(coworkerId);
    }

    /**
     * Execute the role-specific skill of the employee (prints an example action).
     */
    public abstract void executeSkill();

    @Override
    public int compareTo(Employee other) {
        int res = this.lastName.compareToIgnoreCase(other.lastName);
        if (res == 0) {
            return this.firstName.compareToIgnoreCase(other.firstName);
        }
        return res;
    }
}