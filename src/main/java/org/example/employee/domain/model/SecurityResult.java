package org.example.employee.domain.model;

import java.util.List;

public record SecurityResult(
        double totalRiskScore,
        List<ConnectionRisk> connectionRisks // Sorbarendezett lista
) implements EmployeeTaskResults {}