package org.example.employee.domain.model.employeeType.securitySpecialist;

/**
 * Represents the calculated security risk of a specific coworker connection.
 *
 * @param coworkerId    The ID of the coworker.
 * @param name          The name of the coworker.
 * @param score         The calculated risk percentage (0.0 to 100.0).
 * @param coworkerCount The total number of connections the coworker has.
 */
public record ConnectionRisk(Long coworkerId, String name, double score, int coworkerCount) {}
