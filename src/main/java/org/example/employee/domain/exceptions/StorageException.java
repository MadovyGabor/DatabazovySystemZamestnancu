package org.example.employee.domain.exceptions;


/**
 * Custom runtime exception used to wrap and propagate {@link StorageError} instances.
 * Thrown when a data storage or persistence failure occurs.
 */
public class StorageException extends RuntimeException {

    private final StorageError error;

    /**
     * Constructs a new StorageException with just the error type.
     *
     * @param error The underlying storage error.
     */
    public StorageException(StorageError error) {
        super(error.getMessage());
        this.error = error;
    }


    /**
     * Constructs a new StorageException with additional details.
     *
     * @param error   The underlying storage error.
     * @param details Additional context about the error.
     */
    public StorageException(StorageError error, String details) {
        super(error.getMessage() + " -> " + details);
        this.error = error;
    }

    /**
     * Retrieves the underlying {@link StorageError}.
     *
     * @return The storage error type.
     */
    public StorageError getError() {
        return error;
    }
}
