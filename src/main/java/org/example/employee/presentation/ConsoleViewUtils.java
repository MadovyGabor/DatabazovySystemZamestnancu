package org.example.employee.presentation;

import java.util.Scanner;

/**
 * Small helper utilities for reading validated input from the console.
 */
public class ConsoleViewUtils {
    /**
     * Reads a non-empty string from the console.
     *
     * @param scanner the scanner connected to the input source
     * @param prompt  the prompt to display to the user
     * @return a non-empty string provided by the user
     */
    static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Vstup nesmi byt prazdny. Zadejte prosim platny retazec.");
            }
        }
    }

    /**
     * Reads an integer within a specified range from the console.
     *
     * @param scanner      the scanner connected to the input source
     * @param prompt       the prompt to display to the user
     * @param errorMessage the error message to display for invalid input
     * @param min          the minimum acceptable value (inclusive)
     * @param max          the maximum acceptable value (inclusive)
     * @return an integer value within the specified range
     */
    static int readIntInRange(Scanner scanner, String prompt, String errorMessage, int min, int max) {
        while (true) {
            int value = readValidInt(scanner, prompt, errorMessage);
            if (value >= min && value <= max) {
                return value;
            } else {
                System.out.println(errorMessage + " (Musi byt mezi " + min + " a " + max + ")");
            }
        }
    }

    /**
     * Reads a valid integer from the console.
     *
     * @param scanner      the scanner connected to the input source
     * @param prompt       the prompt to display to the user
     * @param errorMessage the error message to display for invalid input
     * @return a valid integer provided by the user
     */
    static int readValidInt(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }

    /**
     * Reads a valid long integer from the console.
     *
     * @param scanner      the scanner connected to the input source
     * @param prompt       the prompt to display to the user
     * @param errorMessage the error message to display for invalid input
     * @return a valid long integer provided by the user
     */
    static Long readValidLong(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }
}
