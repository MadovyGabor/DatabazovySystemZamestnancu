package org.example.employee.domain.model.employeeType.securitySpecialist;

public record ConnectionRisk(Long coworkerId, String name, double score, int coworkerCount) {}
