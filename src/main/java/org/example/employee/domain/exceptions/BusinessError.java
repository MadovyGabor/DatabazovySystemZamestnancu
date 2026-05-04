package org.example.employee.domain.exceptions;


public enum BusinessError {

    EMPLOYEE_NOT_FOUND("Zamestnanec s danym ID nebyl nalezen."),

    DUPLICATE_ID("Zaznam s timto ID jiz v systemu existuje!"),

    SELF_COLLABORATION("Zamestnanec nemuze spolupracovat sam se sebou!"),

    COLLABORATION_ALREADY_EXISTS("Spoluprace mezi temito zamestnanci jiz existuje.");

    private final String message;

    BusinessError(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
