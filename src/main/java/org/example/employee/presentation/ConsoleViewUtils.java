package org.example.employee.presentation;

import java.util.Scanner;

public class ConsoleViewUtils {

    public static void waitForEnter(Scanner scanner) {
        System.out.println("\n[ Stisknete ENTER pro pokracovani... ]");
        scanner.nextLine();
    }

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                // Windows esetén meghívjuk a 'cls' parancsot
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Mac és Linux esetén ANSI kódokat használunk
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // "B-terv", ha valamiért nem működne (pl. furcsa IDE beállítások miatt)
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
