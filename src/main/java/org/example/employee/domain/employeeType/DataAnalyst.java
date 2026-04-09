package org.example.employee.domain.employeeType;

import org.example.employee.domain.Employee;

public class DataAnalyst extends Employee  {

    public DataAnalyst(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void executeSkill() {
        // a) Datoví analytici dokážou určit, s kterým spolupracovníkem mají nejvíce společných spolupracovníků.
        System.out.println(getFirstName() + " (Data Analyst) is calculating the coworker with the most mutual connections...");
        // Ide majd később megírjuk a konkrét logikát, ami végigmegy a listákon
    }
}
