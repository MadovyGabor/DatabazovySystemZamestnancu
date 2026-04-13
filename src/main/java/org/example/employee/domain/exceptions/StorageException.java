package org.example.employee.domain.exceptions;

/**
 * Runtime exception representing storage-related errors (I/O or corrupted data).
 * Wraps a StorageError code and an optional details string included in the message.
 */
public class StorageException extends RuntimeException {

    private final StorageError error;

    public StorageException(StorageError error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Construct a StorageException with additional details (e.g. line number or file path).
     * The details string is appended to the error message.
     */
    public StorageException(StorageError error, String details) {
        super(error.getMessage() + " -> " + details);
        this.error = error;
    }

    public StorageError getError() {
        return error;
    }
}
