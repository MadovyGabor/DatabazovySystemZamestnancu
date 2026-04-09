package org.example.employee.domain.employeeType;

import org.example.employee.domain.Employee;

public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void executeSkill() {
        // b) Bezpečnostní specialisté dokážou vyhodnotit rizikovost spolupráce...
        System.out.println(getFirstName() + " (Security Specialist) is evaluating collaboration risk scores...");
        // Ide is jön majd a specifikus algoritmus
    }
}
