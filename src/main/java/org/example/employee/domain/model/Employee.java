package org.example.employee.domain.model;


import org.example.employee.domain.EmployeeRepository;

import java.util.HashMap;
import java.util.Map;


/**
 * Abstract base class representing an employee in the system.
 * Contains common properties such as ID, name, birth year, and a collection of coworkers.
 * Implements {@link Comparable} to allow sorting by last name, then first name.
 */
public abstract class Employee implements Comparable<Employee>{

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int birthYear;

    private Map<Long, CollaborationLevel> coworkers;
    /**
     * Constructs a new Employee.
     *
     * @param id        The unique identifier for the employee.
     * @param firstName The employee's first name.
     * @param lastName  The employee's last name.
     * @param birthYear The employee's year of birth.
     */
    public Employee(Long id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.coworkers = new HashMap<>();
    }


    /**
     * Retrieves the employee's unique ID.
     * @return The ID.
     */
    public Long getId() {
        return id;
    }


    /**
     * Retrieves the descriptive name of the employee's group.
     * @return The group name.
     */
    public abstract String getGroupName();


    /**
     * Retrieves the short identifier of the employee's group.
     * @return The group ID (e.g., "DA" or "SS").
     */
    public abstract String getGroupId();

    /**
     * Retrieves the employee's first name.
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieves the employee's last name.
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Retrieves the employee's year of birth.
     * @return The birth year.
     */
    public int getBirthYear() {
        return birthYear;
    }


    /**
     * Retrieves the map of coworker connections.
     * @return A map where the key is the coworker's ID and the value is the {@link CollaborationLevel}.
     */
    public Map<Long, CollaborationLevel> getCoworkers() {
        return coworkers;
    }

    /**
     * Adds a coworker connection.
     * @param coworkerId The ID of the coworker.
     * @param level      The level of collaboration.
     */
    public void addCoworker(Long coworkerId, CollaborationLevel level) {
        this.coworkers.put(coworkerId, level);
    }

    /**
     * Removes a coworker connection.
     * @param coworkerId The ID of the coworker to remove.
     */
    public void removeCoworker(Long coworkerId) {
        this.coworkers.remove(coworkerId);
    }


    /**
     * Executes the specialized skill of this employee type.
     * @param repository The repository to access other employee data if needed.
     * @return The results of the skill execution.
     */
    public abstract EmployeeTaskResults executeSkill(EmployeeRepository repository);

    /**
     * Compares this employee with another based on last name, then first name.
     * @param other The other employee to compare with.
     * @return A negative integer, zero, or a positive integer as this employee is less than, equal to, or greater than the specified employee.
     */
    @Override
    public int compareTo(Employee other) {
        int res = this.lastName.compareToIgnoreCase(other.lastName);
        if (res == 0) {
            return this.firstName.compareToIgnoreCase(other.firstName);
        }
        return res;
    }
}