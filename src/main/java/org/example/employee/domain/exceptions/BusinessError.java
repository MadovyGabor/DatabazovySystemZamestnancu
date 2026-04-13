package org.example.employee.domain.exceptions;

/**
 * Business error codes used to signal domain validation and business rule violations.
 * Each enum value contains a human-readable message (currently in Czech).
 */
public enum BusinessError {
    // Employee not found in repository
    EMPLOYEE_NOT_FOUND("Zamestnanec s danym ID nebyl nalezen."),
    // Duplicate id attempted to be added
    DUPLICATE_ID("Zaznam s timto ID jiz v systemu existuje!"),
    // Attempt to create a self-collaboration
    SELF_COLLABORATION("Zamestnanec nemuze spolupracovat sam se sebou!"),
    // Collaboration already exists between two employees
    COLLABORATION_ALREADY_EXISTS("Spoluprace mezi temito zamestnanci jiz existuje.");

    private final String message;

    BusinessError(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
