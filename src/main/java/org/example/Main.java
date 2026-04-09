package org.example;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.Employee;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.employeeType.DataAnalyst;
import org.example.employee.presentation.EmployeeConsoleView;

import java.util.Scanner;

import static org.example.employee.presentation.EmployeeConsoleView.printEmployeeDetails;

public class Main {

    public static void main(String[] args) {

        EmployeeRepository repository = new InMemoryEmployeeRepository();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        System.out.println("Vitejte v Databazi zamestnancu!");
        EmployeeConsoleView.loadFromFile(scanner, repository);

        // l) Nacteni vsech dat z SQL databaze pri spusteni programu
        System.out.println("[Info] Nacitam data z SQL databaze... (Zatim neimplementovano)");

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
            System.out.println("k) Ukoncit program a ulozit do SQL");
            System.out.print("\nVyberte moznost (a-k): ");

            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
                case "a":
                    EmployeeConsoleView.addNewEmployee(scanner, repository);
                    break;
                case "b":
                    EmployeeConsoleView.addCollaboration(scanner, repository);
                    break;
                case "c":
                    EmployeeConsoleView.removeEmployee(scanner, repository);
                    break;
                case "d":
                    System.out.print("Zadejte ID zamestnance: ");
                    try {
                        Long id = Long.parseLong(scanner.nextLine());
                        Employee emp = repository.getEmployeeById(id);

                        if (emp != null) {
                            printEmployeeDetails(emp, scanner);
                        } else {
                            System.out.println("X Zamestnanec s ID " + id + " nebyl nalezen.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("X Neplatne ID! Musi to byt cislo.");
                    }
                    break;
                case "e":
                    EmployeeConsoleView.employeeWork(scanner, repository);
                    break;
                case "f":
                    EmployeeConsoleView.printAllEmployeesByGroup(repository);
                    break;
                case "g":
                    EmployeeConsoleView.printStatistics(repository);
                    break;
                case "h":
                    EmployeeConsoleView.printEmployeeCounts(repository);
                    break;
                case "i":
                    EmployeeConsoleView.saveToFile(scanner, repository);
                    break;
                case "j":
                    EmployeeConsoleView.loadFromFile(scanner, repository);
                    break;
                case "k":
                    System.out.println("Ukladam data do SQL databaze... (Zatim neimplementovano)");
                    System.out.println("Ukoncovani programu. Na shledanou!");
                    running = false;
                    break;
                default:
                    System.out.println("X Neplatna volba! Zadejte pismeno od 'a' do 'k'.");
            }
        }
        scanner.close();
    }


}