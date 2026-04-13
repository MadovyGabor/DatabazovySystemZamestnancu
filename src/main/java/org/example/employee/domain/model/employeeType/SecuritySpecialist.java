package org.example.employee.domain.model.employeeType;

import org.example.employee.domain.model.Employee;

/**
 * Employee subtype representing a Security Specialist.
 * Prints a sample action when executeSkill() is invoked.
 */
public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void executeSkill() {
        // Example role-specific action text printed to console
        System.out.println(getFirstName() + " (Security Specialist) is evaluating collaboration risk scores...");
    }

    @Override
    public String getGroupName() {
        return "Bezpecnostni Specialista";
    }

    @Override
    public String getGroupId() {
        return "SS";
    }
}
