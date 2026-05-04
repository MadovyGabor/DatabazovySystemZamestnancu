package org.example.employee.presentation;

import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.*;
import org.example.employee.domain.EmployeeService;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.dataAnalyst.AnalystResult;
import org.example.employee.domain.model.employeeType.securitySpecialist.ConnectionRisk;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecurityResult;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.example.employee.domain.exceptions.BusinessException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.example.Main.DATABASE_FILE;

/**
 * Console-based view for user interactions. Methods are static helpers invoked
 * from the main menu.
 * The class focuses on reading user input, invoking the service and printing
 * results.
 */
public class EmployeeConsoleView {

    /**
     * Searches for an employee by their ID, reads the ID from the console, and
     * displays the employee details
     * or an error message if not found.
     */
    public static void searchEmployeeByID(Scanner scanner, EmployeeService service) {
        System.out.println("\n--- VYHLEDANI ZAMESTNANCE DLE ID ---");
        long inputID = ConsoleViewUtils.readValidLong(scanner, "Zadejte ID zamestnance: ",
                "X Neplatne ID! Musi to byt cislo. Zkuste to znovu.");
        try {
            Employee emp = service.getEmployeeById(inputID);
            printEmployeeDetails(emp, service);
        } catch (BusinessException e) {
            System.out.println("X CHYBA [" + e.getError().name() + "]: " + e.getMessage());
        }
    }

    /**
     * Adds a new employee by reading employee details from the console.
     * The user must specify the group (Data Analyst or Security Specialist) and
     * provide personal details.
     * The employee is then added to the system, and a success or error message is
     * displayed.
     */
    public static void addNewEmployee(Scanner scanner, EmployeeService service) {
        System.out.println("\n--- PRIDANI NOVEHO ZAMESTNANCE ---");
        String groupChoice;
        do {
            System.out.println("Vyberte skupinu:");
            System.out.println("D - Datovy analytik");
            System.out.println("B - Bezpecnostni specialista");
            System.out.print("Vase volba: ");
            groupChoice = scanner.nextLine().trim();

            if (groupChoice.equalsIgnoreCase("d") || groupChoice.equalsIgnoreCase("b")) {
                break;
            }
            System.out.println("X Neplatna volba skupiny! Zkuste to znovu.");
        } while (true);

        String firstName = ConsoleViewUtils.readNonEmptyString(scanner, "Zadejte jmeno: ");
        String lastName = ConsoleViewUtils.readNonEmptyString(scanner, "Zadejte prijmeni: ");

        int birthYear = ConsoleViewUtils.readIntInRange(scanner, "Zadejte rok narozeni: ",
                "X Neplatny rok! Musi to byt cele cislo. Zkuste to znovu.", 1900, 2100);

        Long newId = service.getNextId();
        Employee newEmployee = groupChoice.equalsIgnoreCase("d")
                ? new DataAnalyst(newId, firstName, lastName, birthYear)
                : new SecuritySpecialist(newId, firstName, lastName, birthYear);

        try {
            service.addEmployee(newEmployee);
            System.out.println("V Zamestnanec uspesne pridan s ID: " + newId);
        } catch (BusinessException e) {
            System.out.println("X CHYBA [" + e.getError().name() + "]: " + e.getMessage());
        }
    }

    /**
     * Prints detailed information about an employee, including coworker names.
     * Uses the Service to resolve names from IDs stored in the coworker map.
     */
    public static void printEmployeeDetails(Employee emp, EmployeeService service) {
        System.out.println("\n=========================================");
        System.out.println("   DETAIL ZAMESTNANCE #" + emp.getId());
        System.out.println("=========================================");
        System.out.println(" Jmeno a prijmeni : " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println(" Rok narozeni     : " + emp.getBirthYear());
        System.out.println(" Skupina          : " + emp.getGroupName());
        System.out.println("-----------------------------------------");
        System.out.println("   SPOLUPRACOVNICI (" + emp.getCoworkers().size() + ")");

        if (emp.getCoworkers().isEmpty()) {
            System.out.println("   Zadne vazby.");
        } else {
            // Iterate through the map of coworker IDs and levels
            emp.getCoworkers().forEach((coworkerId, level) -> {
                String coworkerName;
                try {
                    // Resolve the name using the service
                    Employee coworker = service.getEmployeeById(coworkerId);
                    coworkerName = coworker.getFirstName() + " " + coworker.getLastName();
                } catch (Exception e) {
                    // Fallback if the employee is missing from the database
                    coworkerName = "Unknown Employee";
                }

                // Print formatted row: Name (ID) | Level (Czech)
                System.out.printf("   - %-20s (ID: %d) | Uroven: %s%n",
                        coworkerName, coworkerId, level.toCzech());
            });
        }
        System.out.println("=========================================");
    }

    /**
     * Adds a collaboration between two employees by reading their IDs and the
     * collaboration level from the console.
     * The system validates the IDs and ensures that an employee cannot collaborate
     * with themselves.
     */
    public static void addCollaboration(Scanner scanner, EmployeeService service) {
        System.out.println("\n--- PRIDANI SPOLUPRACE ---");
        Long empId;
        Long coworkerId;

        // Employee ID input
        while (true) {
            empId = ConsoleViewUtils.readValidLong(scanner, "Zadejte ID zamestnance (0 pro zruseni): ",
                    "X Neplatne ID! Musi to byt cislo. Zkuste to znovu.");
            if (empId == 0)
                return;
            try {
                service.getEmployeeById(empId);
                break;
            } catch (BusinessException e) {
                System.out.println("X CHYBA: " + e.getMessage());
            }
        }

        // Coworker ID input
        while (true) {
            coworkerId = ConsoleViewUtils.readValidLong(scanner, "Zadejte ID kolegy (0 pro zruseni): ",
                    "X Neplatne ID! Musi to byt cislo. Zkuste to znovu.");
            if (coworkerId == 0)
                return;
            try {
                service.getEmployeeById(coworkerId);
                if (empId.equals(coworkerId)) {
                    System.out.println("X Zamestnanec nemuze spolupracovat sam se sebou!");
                    continue;
                }
                break;
            } catch (BusinessException e) {
                System.out.println("X CHYBA: " + e.getMessage());
            }
        }

        // Collaboration level input - Labels matching your enum output logic
        System.out.println("Vyberte uroven spoluprace (1-Spatna, 2-Prumerna, 3-Dobra):");
        int levelChoice = ConsoleViewUtils.readIntInRange(scanner, "Vase volba: ", "X Neplatne!", 1, 3);

        // Using your static fromInt method
        CollaborationLevel level = CollaborationLevel.fromInt(levelChoice);

        try {
            service.addCollaboration(empId, coworkerId, level);
            System.out.println("V Spoluprace uspesne navazana.");
        } catch (BusinessException e) {
            System.out.println("X CHYBA [" + e.getError().name() + "]: " + e.getMessage());
        }
    }

    /**
     * Removes an employee from the system by their ID.
     * The ID is read from the console, and the employee is removed along with all
     * their collaborations.
     * Success or error messages are displayed accordingly.
     */
    public static void removeEmployee(Scanner scanner, EmployeeService service) {
        System.out.println("\n--- ODEBRANI ZAMESTNANCE ---");
        while (true) {
            Long id = ConsoleViewUtils.readValidLong(
                    scanner,
                    "Zadejte ID zamestnance k odstraneni (0 pro zruseni): ",
                    "X Neplatne ID! Musi to byt cislo.");
            if (id == 0) {
                System.out.println("Operace zrusena.");
                return;
            }
            try {
                service.removeEmployeeById(id);
                System.out.println("V Zamestnanec (a vsechny jeho vazby) byl uspesne odstranen.");
                break;
            } catch (BusinessException e) {
                System.out.println("X CHYBA: " + e.getMessage());
                System.out.println("Zkuste to znovu.");
            }
        }
    }

    /**
     * Saves the current employee data to a file.
     * Displays a success message if the data is saved correctly, or an error
     * message if there is a problem.
     */
    public static void saveToFile(EmployeeService service) {
        System.out.println("\n--- ULOZENI DO SOUBORU ---");
        try {
            service.saveData();
            System.out.println("V Data uspesne ulozena do " + DATABASE_FILE);
        } catch (StorageException e) { // <-- CSAK A STORAGE HIBÁKAT KAPJUK EL
            System.out.println("X CHYBA PRI UKLADANI [" + e.getError().name() + "]: " + e.getMessage());
        }
    }

    /**
     * Loads employee data from a file.
     * Displays a success message if the data is loaded correctly, or an error
     * message if there is a problem.
     */
    public static void loadFromFile(EmployeeService service) {
        System.out.println("\n--- NACTENI ZE SOUBORU ---");
        try {
            service.loadData();
            System.out.println("V Data uspesne nactena ze souboru " + DATABASE_FILE);
        } catch (StorageException e) { // <-- ITT IS
            System.out.println("X CHYBA PRI NACITANI [" + e.getError().name() + "]: " + e.getMessage());
        }
    }

    /**
     * Executes an employee's skill and displays the results.
     * This version includes names for better context and uses English comments.
     */
    public static void employeeWork(Scanner scanner, EmployeeService service) {
        System.out.println("\n--- SPUSTENI DOVEDNOSTI ZAMESTNANCE ---");
        System.out.print("Zadejte ID zamestnance: ");
        Long empId;

        // 1. Validate ID input
        try {
            empId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("X Neplatne ID! Zadejte prosim cislo.");
            return;
        }

        try {
            // 2. Delegate business logic to Service and receive a Result Record
            EmployeeTaskResults result = service.executeEmployeeSkill(empId);
            System.out.println("\n=== [ VYSLEDEK ANALYZY ] ===");

            // 3. Handle specific results using Java Pattern Matching
            switch (result) {
                case AnalystResult a -> {
                    if (a.bestMatchId() == null) {
                        System.out.println("[-] Datovy analytik: Zamestnanec nema zadne spolupracovniky k analyze.");
                    } else {
                        // Show both Name and ID for better readability
                        System.out.printf("[+] Nejlepsi shoda: %s (ID: %d)%n",
                                a.bestMatchName(), a.bestMatchId());
                        System.out.printf("[+] Pocet spolecnych kontaktu: %d%n", a.commonCount());
                    }
                }
                case SecurityResult s -> {
                    System.out.printf("[!] Celkove prumerne riziko auditovane skupiny: %.1f%%%n", s.totalRiskScore());
                    System.out.println("[!] Detaily auditu (serazeno podle rizika):");

                    if (s.connectionRisks().isEmpty()) {
                        System.out.println("    [-] Zadne cile k auditu (izolovany zamestnanec).");
                    } else {
                        // Iterate through identified risks
                        for (ConnectionRisk risk : s.connectionRisks()) {
                            // Determine the visual severity tag
                            String alert = risk.score() >= 70 ? "[CRITICAL]"
                                    : (risk.score() >= 40 ? "[WARNING] " : "[OK]      ");

                            // Output formatted table-like row: Alert | Name (ID) | Risk % | Connection Count
                            System.out.printf("    %s %-20s (ID %d) | Riziko: %5.1f%% | Vazby: %d%n",
                                    alert, risk.name(), risk.coworkerId(), risk.score(), risk.coworkerCount());
                        }
                    }
                }
                // 4. Default case for forward compatibility (failsafe)
                default -> System.out.println("[-] Neznamy typ vysledku.");
            }
            System.out.println("============================\n");

        } catch (BusinessException e) {
            // Handle domain-specific errors (e.g., Employee not found)
            System.out.println("X CHYBA: " + e.getMessage());
        }
    }

    /**
     * Prints all employees sorted by their group (Data Analysts and Security
     * Specialists).
     * Displays a message if no employees are found in a group.
     */
    public static void printAllEmployeesByGroup(EmployeeService service) {
        System.out.println("\n--- ABECEDNI VYPIS ZAMESTNANCU (Dle skupin) ---");
        List<Employee> analysts = service.getEmployeesByTypeSorted("DA");
        List<Employee> specialists = service.getEmployeesByTypeSorted("SS");

        System.out.println("\n[ DATOVI ANALYTICI ]");
        if (analysts.isEmpty())
            System.out.println("Zadni analytici v systemu.");
        else
            analysts.forEach(
                    e -> System.out.println(e.getLastName() + " " + e.getFirstName() + " (ID: " + e.getId() + ")"));

        System.out.println("\n[ BEZPECNOSTNI SPECIALISTE ]");
        if (specialists.isEmpty())
            System.out.println("Zadni specialiste v systemu.");
        else
            specialists.forEach(
                    e -> System.out.println(e.getLastName() + " " + e.getFirstName() + " (ID: " + e.getId() + ")"));
    }

    /**
     * Prints various statistics about the employees in the system,
     * such as the employee with the most connections and the most frequent
     * collaboration level.
     */
    public static void printStatistics(EmployeeService service) {
        System.out.println("\n--- FIREMNI STATISTIKY ---");
        Employee topEmp = service.getEmployeeWithMostConnections();

        System.out.print("[ Zamestnanec s nejvice vazbami ] -> ");
        if (topEmp != null && !topEmp.getCoworkers().isEmpty()) {
            System.out.println(topEmp.getFirstName() + " " + topEmp.getLastName() +
                    " (Pocet spojeni: " + topEmp.getCoworkers().size() + ")");
        } else {
            System.out.println("Zadne vazby v systemu.");
        }
        System.out.println("[ Prevajujici kvalita spoluprace ] -> " + service.getMostFrequentCollaborationLevel());
    }

    /**
     * Prints the count of employees in each group (Data Analysts and Security
     * Specialists).
     */
    public static void printEmployeeCounts(EmployeeService service) {
        System.out.println("\n--- POCET ZAMESTNANCU VE SKUPINACH ---");
        Map<String, Integer> counts = service.getEmployeeCountsByGroup();

        int da = counts.getOrDefault("DA", 0);
        int ss = counts.getOrDefault("SS", 0);

        System.out.println("[ Datovi analytici ]: " + da);
        System.out.println("[ Bezpecnostni specialiste ]: " + ss);
        System.out.println("--------------------------------------");
        System.out.println("CELKEM V SYSTEMU: " + (da + ss));
    }
}
