package org.example.employee.domain.model.employeeType.securitySpecialist;

import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.List;

public record SecurityResult(
        double totalRiskScore,
        List<ConnectionRisk> connectionRisks // Sorbarendezett lista
) implements EmployeeTaskResults {}