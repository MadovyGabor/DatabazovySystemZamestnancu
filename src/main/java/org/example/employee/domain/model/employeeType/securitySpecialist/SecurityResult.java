package org.example.employee.domain.model.employeeType.securitySpecialist;

import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.List;

/**
 * Represents the results of a Security Specialist's skill execution.
 *
 * @param totalRiskScore  The average risk score across all connections.
 * @param connectionRisks A list of individual risks for each connected coworker.
 */
public record SecurityResult(
        double totalRiskScore,
        List<ConnectionRisk> connectionRisks
) implements EmployeeTaskResults {}