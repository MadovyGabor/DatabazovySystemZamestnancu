package org.example.employee.domain.model.employeeType;

import org.example.employee.domain.model.Employee;

/**
 * Employee subtype representing a Data Analyst.
 * Prints a sample action when executeSkill() is invoked.
 */
public class DataAnalyst extends Employee  {

    public DataAnalyst(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void executeSkill() {
        // Example role-specific action text printed to console
        System.out.println(getFirstName() + " (Data Analyst) is calculating the coworker with the most mutual connections...");
    }

    @Override
    public String getGroupName() {
        return "Datovy Analytik";
    }

    @Override
    public String getGroupId() {
        return "DA";
    }
}
