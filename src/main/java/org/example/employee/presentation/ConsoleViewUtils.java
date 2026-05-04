package org.example.employee.presentation;

import java.util.Scanner;


public class ConsoleViewUtils {

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
