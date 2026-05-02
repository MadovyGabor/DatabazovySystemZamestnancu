package org.example.employee.domain.model;

public record ConnectionRisk(Long coworkerId, String name, double score, int coworkerCount) {}
