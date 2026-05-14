package org.example.employee.domain.exceptions;


/**
 * Enumeration of possible storage-related errors that can occur during persistence operations.
 */
public enum StorageError {
    /** General error during reading. */
    FILE_READ_ERROR("Chyba pri cteni ze souboru."),
    /** Error when permissions do not allow reading or writing. */
    FILE_ACCESS_DENIED("Chyba pri pristupu k souboru."),
    /** Error when the specified file does not exist. */
    FILE_NOT_FOUND("Soubor nebyl nalezen."),
    /** Error when data parsing fails or format is invalid. */
    DATA_CORRUPTED("Data v souboru jsou poškozena nebo ve špatném formátu na radku: "),
    /** General error during writing. */
    FILE_WRITE_ERROR("Chyba pri ukladani do souboru."),
    /** General database or persistence error. */
    PERSISTENCE_ERROR("Chyba pri praci s databazi nebo jinym ulozistem.");

    private final String message;

    StorageError(String message) {
        this.message = message;
    }

    /**
     * Retrieves the descriptive message associated with the storage error.
     *
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}
