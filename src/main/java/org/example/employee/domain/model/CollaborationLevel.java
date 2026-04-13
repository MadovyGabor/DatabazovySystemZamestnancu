package org.example.employee.domain.model;

/**
 * Enum representing collaboration quality levels used in coworker relationships.
 * Levels are ordered from BAD to GOOD.
 */
public enum CollaborationLevel {
    BAD,        // špatná
    AVERAGE,    // průmA�rná
    GOOD;     // dobrá

    /**
     * Convert a numeric level (1..3) to a CollaborationLevel.
     *
     * @param level numeric level where 1=BAD, 2=AVERAGE, 3=GOOD
     * @return corresponding CollaborationLevel
     * @throws IllegalArgumentException if the number is outside 1..3
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
