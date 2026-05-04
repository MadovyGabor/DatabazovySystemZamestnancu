package org.example.employee.domain.model.employeeType.dataAnalyst;

import org.example.employee.domain.model.EmployeeTaskResults;

public record AnalystResult(Long bestMatchId, String bestMatchName, int commonCount) implements EmployeeTaskResults { }
