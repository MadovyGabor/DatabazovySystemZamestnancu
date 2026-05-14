package org.example.employee.domain.model;


/**
 * Represents the quality of collaboration between two coworkers.
 */
public enum CollaborationLevel {
    BAD,
    AVERAGE,
    GOOD;


    /**
     * Translates the collaboration level to a Czech string.
     *
     * @return The Czech translation.
     */
    public String toCzech() {
        return switch (this) {
            case BAD -> "spatna";
            case AVERAGE -> "prumerna";
            case GOOD -> "dobra";
        };
    }


    /**
     * Converts an integer value to the corresponding CollaborationLevel.
     *
     * @param level The integer value (1 for BAD, 2 for AVERAGE, 3 for GOOD).
     * @return The corresponding CollaborationLevel.
     * @throws IllegalArgumentException If the integer is not 1, 2, or 3.
     */
    public static CollaborationLevel fromInt(int level) {
        return switch (level) {
            case 1 -> BAD;
            case 2 -> AVERAGE;
            case 3 -> GOOD;
            default -> throw new IllegalArgumentException("Neplatna uroven");
        };
    }
}