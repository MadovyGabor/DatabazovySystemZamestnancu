package org.example.employee.domain.exceptions;


public class StorageException extends RuntimeException {

    private final StorageError error;

    public StorageException(StorageError error) {
        super(error.getMessage());
        this.error = error;
    }


    public StorageException(StorageError error, String details) {
        super(error.getMessage() + " -> " + details);
        this.error = error;
    }

    public StorageError getError() {
        return error;
    }
}
