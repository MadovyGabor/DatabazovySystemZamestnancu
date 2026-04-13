package org.example;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.EmployeeService;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.presentation.EmployeeConsoleView;
import org.example.employee.data.FileEmployeeStorage;

import java.util.Scanner;

/**
 * Main application entry point. Sets up repository, service and the console UI.
 */
public class Main {

    // Default filename used for persisting employee data.
    public static final String DATABASE_FILE = "database.txt";

    public static void main(String[] args) {
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository, new FileEmployeeStorage(DATABASE_FILE));

        Scanner scanner = new Scanner(System.in);

        System.out.println("Vitejte v Databazi zamestnancu!");
        try {
            service.loadData();
            System.out.println("V Data uspesne nactena ze souboru po startu.");
        } catch (StorageException e) {
            System.out.println("! Upozorneni: Nepodarilo se nacist data (" + e.getMessage() + "). Zaciname s cistou databazi.");
        }

        boolean running = true;
        while (running) {
            System.out.println("\n--- HLAVNI MENU ---");
            System.out.println("a) Pridani zamestnance");
            System.out.println("b) Pridani spoluprace");
            System.out.println("c) Odebrani zamestnance");
            System.out.println("d) Vyhledani zamestnance dle ID");
            System.out.println("e) Spusteni dovednosti zamestnance");
            System.out.println("f) Abecedni vypis zamestnancu podle prijmeni ve skupinach");
            System.out.println("g) Statistiky spoluprace");
            System.out.println("h) Vypis poctu zamestnancu ve skupinach");
            System.out.println("i) Ulozeni zamestnance do souboru");
            System.out.println("j) Nacteni zamestnance ze souboru");
            System.out.println("k) Ukoncit program a ulozit do souboru"); // Kijavítottam az "SQL" elírást
            System.out.print("\nVyberte moznost (a-k): ");

            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "a" -> EmployeeConsoleView.addNewEmployee(scanner, service);
                case "b" -> EmployeeConsoleView.addCollaboration(scanner, service);
                case "c" -> EmployeeConsoleView.removeEmployee(scanner, service);
                case "d" -> EmployeeConsoleView.searchEmployeeByID(scanner, service);
                case "e" -> EmployeeConsoleView.employeeWork(scanner, service);
                case "f" -> EmployeeConsoleView.printAllEmployeesByGroup(service);
                case "g" -> EmployeeConsoleView.printStatistics(service);
                case "h" -> EmployeeConsoleView.printEmployeeCounts(service);
                case "i" -> EmployeeConsoleView.saveToFile(service);
                case "j" -> EmployeeConsoleView.loadFromFile(service);
                case "k" -> {
                    System.out.println("Ukladam data pred ukoncenim...");
                    EmployeeConsoleView.saveToFile(service);
                    System.out.println("Ukoncovani programu. Na shledanou!");
                    running = false;
                }
                default -> System.out.println("X Neplatna volba! Zadejte pismeno od 'a' do 'k'.");
            }
        }
        scanner.close();
    }
}