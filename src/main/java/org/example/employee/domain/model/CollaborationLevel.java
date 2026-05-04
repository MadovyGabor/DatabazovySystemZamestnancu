package org.example.employee.domain.model;


public enum CollaborationLevel {
    BAD,
    AVERAGE,
    GOOD;


    public String toCzech() {
        return switch (this) {
            case BAD -> "spatna";
            case AVERAGE -> "prumerna";
            case GOOD -> "dobra";
        };
    }


    public static CollaborationLevel fromInt(int level) {
        return switch (level) {
            case 1 -> BAD;
            case 2 -> AVERAGE;
            case 3 -> GOOD;
            default -> throw new IllegalArgumentException("Neplatna uroven");
        };
    }
}