package org.example.employee.presentation;

import org.example.employee.domain.CollaborationLevel;
import org.example.employee.domain.Employee;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.employeeType.DataAnalyst;
import org.example.employee.domain.employeeType.SecuritySpecialist;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeeConsoleView {

    // --- AZ ÚJ METÓDUS AZ ALKALMAZOTT HOZZÁADÁSÁRA ---
    public static void addNewEmployee(Scanner scanner, EmployeeRepository repository) {
        System.out.println("\n--- PRIDANI NOVEHO ZAMESTNANCE ---");
        System.out.println("Vyberte skupinu:");
        System.out.println("1 - Datovy analytik");
        System.out.println("2 - Bezpecnostni specialista");
        System.out.print("Vase volba (1/2): ");

        String groupChoice = scanner.nextLine();
        if (!groupChoice.equals("1") && !groupChoice.equals("2")) {
            System.out.println("X Neplatna volba skupiny! Zkuste to znovu.");
            return; // Kilépünk a metódusból, visszatér a menübe
        }

        System.out.print("Zadejte jmeno: ");
        String firstName = scanner.nextLine();

        System.out.print("Zadejte prijmeni: ");
        String lastName = scanner.nextLine();

        System.out.print("Zadejte rok narozeni: ");
        int birthYear;
        try {
            birthYear = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("X Neplatny rok narozeni! Musi to byt cislo.");
            return; // Kilépünk a metódusból hiba esetén
        }

        Long newId = repository.getNextId();
        Employee newEmployee;

        if (groupChoice.equals("1")) {
            newEmployee = new DataAnalyst(newId, firstName, lastName, birthYear);
        } else {
            newEmployee = new SecuritySpecialist(newId, firstName, lastName, birthYear);
        }

        repository.addEmployee(newEmployee);
        System.out.println("V Zamestnanec uspesne pridan s ID: " + newId);
    }

    public static void printEmployeeDetails(Employee emp, Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("   DETAIL ZAMESTNANCE #" + emp.getId());
        System.out.println("=========================================");
        System.out.println(" Jmeno a prijmeni : " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println(" Rok narozeni     : " + emp.getBirthYear());

        String group = (emp instanceof DataAnalyst) ? "Datovy analytik" : "Bezpecnostni specialista";
        System.out.println(" Skupina          : " + group);

        System.out.println("-----------------------------------------");
        System.out.println("   SPOLUPRACOVNICI (" + emp.getCoworkers().size() + ")");

        if (emp.getCoworkers().isEmpty()) {
            System.out.println("   Zadne vazby.");
        } else {
            emp.getCoworkers().forEach((coworkerId, level) -> {
                System.out.println("   - ID Kolegy: " + coworkerId + " | Uroven: " + level);
            });
        }
        System.out.println("=========================================");
    }

    // --- ÚJ METÓDUS: EGYÜTTMŰKÖDÉS HOZZÁADÁSA ---
    public static void addCollaboration(Scanner scanner, EmployeeRepository repository) {
        System.out.println("\n--- PRIDANI SPOLUPRACE ---");

        // 1. Alkalmazott ID bekérése
        System.out.print("Zadejte ID zamestnance: ");
        Long empId;
        try {
            empId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("X Neplatne ID!");
            return;
        }

        // Ellenőrizzük, hogy létezik-e az alkalmazott
        Employee employee = repository.getEmployeeById(empId);
        if (employee == null) {
            System.out.println("X Zamestnanec s ID " + empId + " neexistuje.");
            return;
        }

        // 2. Kolléga ID bekérése
        System.out.print("Zadejte ID kolegy: ");
        Long coworkerId;
        try {
            coworkerId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("X Neplatne ID!");
            return;
        }

        // Biztonsági ellenőrzések a kollégára
        if (empId.equals(coworkerId)) {
            System.out.println("X Zamestnanec nemuze spolupracovat sam se sebou!");
            return;
        }

        Employee coworker = repository.getEmployeeById(coworkerId);
        if (coworker == null) {
            System.out.println("X Kolega s ID " + coworkerId + " neexistuje. Nejdriv ho musite pridat.");
            return;
        }

        // 3. Együttműködés szintjének bekérése
        System.out.println("Vyberte uroven spoluprace:");
        System.out.println("1 - Spatna (Bad)");
        System.out.println("2 - Prumerna (Average)");
        System.out.println("3 - Dobra (Good)");
        System.out.print("Vase volba (1-3): ");

        String levelChoice = scanner.nextLine();
        CollaborationLevel level;

        switch (levelChoice) {
            case "1":
                level = CollaborationLevel.BAD;
                break;
            case "2":
                level = CollaborationLevel.AVERAGE;
                break;
            case "3":
                level = CollaborationLevel.GOOD;
                break;
            default:
                System.out.println("X Neplatna volba urovne! Zkuste to znovu.");
                return;
        }

        // ZSENIALIS JAVITAS: Hozzaadjuk MINDKET emberhez!
        employee.addCoworker(coworkerId, level);
        coworker.addCoworker(empId, level); // Ezt a sort kellett betenni!

        System.out.println("V Spoluprace uspesne pridana pro oba zamestnance!");
    }

    // --- ÚJ METÓDUS: ALKALMAZOTT TÖRLÉSE ---
    public static void removeEmployee(Scanner scanner, EmployeeRepository repository) {
        System.out.println("\n--- ODEBRANI ZAMESTNANCE ---");
        System.out.print("Zadejte ID zamestnance k odstraneni: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Megnézzük, létezik-e egyáltalán
            Employee emp = repository.getEmployeeById(id);
            if (emp == null) {
                System.out.println("X Zamestnanec s ID " + id + " neexistuje.");
                return;
            }

            // Ha létezik, töröljük (a Repository intézi a kapcsolatok takarítását is!)
            repository.removeEmployeeById(id);
            System.out.println("V Zamestnanec (a vsechny jeho vazby) byl uspesne odstranen.");

        } catch (NumberFormatException e) {
            System.out.println("X Neplatne ID! Musi to byt cislo.");
        }
    }

    //TODO: ATT rakni kulon classba, mert ez mar tul sok lesz a ConsoleView-nak, de most ide teszem, hogy lathato legyen a main-ben is a menu-ban

    // --- i) MENTÉS FÁJLBA ---
    public static void saveToFile(Scanner scanner, EmployeeRepository repository) {
        System.out.println("\n--- ULOZENI DO SOUBORU ---");
//        System.out.print("Zadejte nazev souboru (napr. data.txt): ");
//        String filename = scanner.nextLine();

        repository.saveToFile("database.txt");
        System.out.println("V Data uspesne ulozena do " + "database.txt");
    }

    // --- j) BETÖLTÉS FÁJLBÓL ---
    public static void loadFromFile(Scanner scanner, EmployeeRepository repository) {
        System.out.println("\n--- NACTENI ZE SOUBORU ---");
//        System.out.print("Zadejte nazev souboru k nacteni (napr. data.txt): ");
//        String filename = scanner.nextLine();

        repository.loadFromFile("database.txt");
        System.out.println("V Data uspesne nactena ze souboru " + "database.txt");
    }

    public static void employeeWork(Scanner scanner, EmployeeRepository repository){

        System.out.println("\n--- SPUSTENI DOVEDNOSTI ZAMESTNANCE ---");
        System.out.print("Zadejte ID zamestnance: ");
        Long empId;
        try {
            empId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("X Neplatne ID!");
            return;
        }

        Employee employee = repository.getEmployeeById(empId);
        if (employee == null) {
            System.out.println("X Zamestnanec s ID " + empId + " neexistuje.");
            return;
        }

        System.out.println("V Spoustim dovednost pro zamestnance: " + employee.getFirstName() + " " + employee.getLastName());
        employee.executeSkill();
    }

    public static void printAllEmployeesByGroup(EmployeeRepository repository) {
        System.out.println("\n--- ABECEDNI VYPIS ZAMESTNANCU (Dle skupin) ---");

        // 1. Lekérjük a két listát - a Repository már rendezi őket ABC-be!
        // Itt a logika marad, csak a UI szöveg változik
        List<Employee> analysts = repository.getEmployeesByTypeSorted("DA");
        List<Employee> specialists = repository.getEmployeesByTypeSorted("SS");

        // 2. Kiiras: Adatkezelok
        System.out.println("\n[ DATOVI ANALYTICI ]");
        if (analysts.isEmpty()) {
            System.out.println("Zadni analytici v systemu.");
        } else {
            for (Employee e : analysts) {
                System.out.println(e.getLastName() + " " + e.getFirstName() + " (ID: " + e.getId() + ")");
            }
        }

        // 3. Kiiras: Biztonsagiak
        System.out.println("\n[ BEZPECNOSTNI SPECIALISTE ]");
        if (specialists.isEmpty()) {
            System.out.println("Zadni specialiste v systemu.");
        } else {
            for (Employee e : specialists) {
                System.out.println(e.getLastName() + " " + e.getFirstName() + " (ID: " + e.getId() + ")");
            }
        }
    }

    public static void printStatistics(EmployeeRepository repository) {
        System.out.println("\n--- FIREMNI STATISTIKY ---");

        // 1. Legtobb kapcsolat (Core Switch)
        Employee topEmp = repository.getEmployeeWithMostConnections();
        System.out.print("[ Zamestnanec s nejvice vazbami ] -> ");
        if (topEmp != null && topEmp.getCoworkers().size() > 0) {
            System.out.println(topEmp.getFirstName() + " " + topEmp.getLastName() +
                    " (Pocet spojeni: " + topEmp.getCoworkers().size() + ")");
        } else {
            System.out.println("Zadne vazby v systemu.");
        }

        // 2. Leggyakoribb minoseg
        String frequentLevel = repository.getMostFrequentCollaborationLevel();
        System.out.println("[ Prevajujici kvalita spoluprace ] -> " + frequentLevel);
    }

    public static void printEmployeeCounts(EmployeeRepository repository) {
        System.out.println("\n--- POCET ZAMESTNANCU VE SKUPINACH ---");

        Map<String, Integer> counts = repository.getEmployeeCountsByGroup();

        System.out.println("[ Datovi analytici ]: " + counts.get("DA"));
        System.out.println("[ Bezpecnostni specialiste ]: " + counts.get("SS"));

        int total = counts.get("DA") + counts.get("SS");
        System.out.println("--------------------------------------");
        System.out.println("CELKEM V SYSTEMU: " + total);
    }
}
