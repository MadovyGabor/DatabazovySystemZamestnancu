package org.example.employee.domain.exceptions;


/**
 * Custom runtime exception used to wrap and propagate {@link BusinessError} instances.
 * Thrown when a business rule or invariant is violated.
 */
public class BusinessException extends RuntimeException {

    private final BusinessError error;

    /**
     * Constructs a new BusinessException.
     *
     * @param error The specific {@link BusinessError} defining the violation.
     */
    public BusinessException(BusinessError error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Retrieves the underlying {@link BusinessError}.
     *
     * @return The business error type.
     */
    public BusinessError getError() {
        return error;
    }
}
