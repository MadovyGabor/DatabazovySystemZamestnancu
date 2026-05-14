package org.example.employee.presentation;

import java.util.Scanner;


/**
 * Utility class providing helper methods for reading and validating console input.
 */
public class ConsoleViewUtils {

    /**
     * Repeatedly prompts the user until a non-empty string is entered.
     *
     * @param scanner The Scanner to read from.
     * @param prompt  The message to display to the user.
     * @return A valid, non-empty string.
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
     * Repeatedly prompts the user until an integer within the specified range is entered.
     *
     * @param scanner      The Scanner to read from.
     * @param prompt       The message to display.
     * @param errorMessage The message to display on invalid input.
     * @param min          The minimum acceptable value (inclusive).
     * @param max          The maximum acceptable value (inclusive).
     * @return A valid integer within the range.
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
     * Repeatedly prompts the user until a valid integer is entered.
     *
     * @param scanner      The Scanner to read from.
     * @param prompt       The message to display.
     * @param errorMessage The message to display if the input cannot be parsed as an integer.
     * @return A valid integer.
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
     * Repeatedly prompts the user until a valid long is entered.
     *
     * @param scanner      The Scanner to read from.
     * @param prompt       The message to display.
     * @param errorMessage The message to display if the input cannot be parsed as a long.
     * @return A valid long.
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
