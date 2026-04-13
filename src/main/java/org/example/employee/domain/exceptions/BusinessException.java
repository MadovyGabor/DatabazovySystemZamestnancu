package org.example.employee.domain.exceptions;

/**
 * Runtime exception used to report business rule violations.
 * Wraps a BusinessError enum value for programmatic handling and a message for display.
 */
public class BusinessException extends RuntimeException {

    private final BusinessError error;

    public BusinessException(BusinessError error) {
        super(error.getMessage());
        this.error = error;
    }

    public BusinessError getError() {
        return error;
    }
}
