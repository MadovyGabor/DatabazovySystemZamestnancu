package org.example.employee.domain.model.employeeType.dataAnalyst;

import org.example.employee.domain.model.EmployeeTaskResults;

/**
 * Represents the results of a Data Analyst's skill execution.
 *
 * @param bestMatchId   The ID of the coworker with the most mutual connections.
 * @param bestMatchName The name of the coworker with the most mutual connections.
 * @param commonCount   The number of mutual connections.
 */
public record AnalystResult(Long bestMatchId, String bestMatchName, int commonCount) implements EmployeeTaskResults { }
