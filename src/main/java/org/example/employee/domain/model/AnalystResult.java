package org.example.employee.domain.model;

public record AnalystResult(Long bestMatchId, int commonCount) implements EmployeeTaskResults { }
