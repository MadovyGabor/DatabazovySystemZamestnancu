package org.example.employee.domain.exceptions;


/**
 * Enumeration of possible business logic errors that can occur within the application.
 */
public enum BusinessError {

    /** Error indicating that an expected employee could not be found. */
    EMPLOYEE_NOT_FOUND("Zamestnanec s danym ID nebyl nalezen."),

    /** Error indicating an attempt to add an employee with an ID that is already in use. */
    DUPLICATE_ID("Zaznam s timto ID jiz v systemu existuje!"),

    /** Error indicating an invalid attempt to make an employee collaborate with themselves. */
    SELF_COLLABORATION("Zamestnanec nemuze spolupracovat sam se sebou!"),

    /** Error indicating that a collaboration between two employees already exists. */
    COLLABORATION_ALREADY_EXISTS("Spoluprace mezi temito zamestnanci jiz existuje.");

    private final String message;

    BusinessError(String message) {
        this.message = message;
    }
    /**
     * Retrieves the descriptive message associated with the error.
     *
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}
