package org.example.employee.domain.exceptions;

/**
 * Error codes related to storage and file I/O operations.
 * Each enum value holds a descriptive message (currently in Czech).
 */
public enum StorageError {
    FILE_READ_ERROR("Chyba pri cteni ze souboru."),
    FILE_ACCESS_DENIED("Chyba pri pristupu k souboru."),
    FILE_NOT_FOUND("Soubor nebyl nalezen."),
    DATA_CORRUPTED("Data v souboru jsou poškozena nebo ve špatném formátu na radku: "),
    FILE_WRITE_ERROR("Chyba pri ukladani do souboru.");

    private final String message;

    StorageError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
