package org.example.employee.data;

import org.example.employee.domain.CollaborationLevel;
import org.example.employee.domain.Employee;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.employeeType.DataAnalyst;
import org.example.employee.domain.employeeType.SecuritySpecialist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private Map<Long, Employee> employeeMap = new HashMap<>();

    private Long currentMaxId = 0L;

    @Override
    public Long getNextId() {
        currentMaxId++;
        return currentMaxId;
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeMap.put(employee.getId(), employee);

        if (employee.getId() > currentMaxId) {
            currentMaxId = employee.getId();
        }
    }

    //TODO: Error handling, if employee with given id is not found
    @Override
    public Employee getEmployeeById(Long id) {
        // BAM! Nincs for ciklus. Azonnal visszaadja az elemet, vagy null-t, ha nincs.
        return employeeMap.get(id);
    }

    //TODO: Error handling, if employee with given id is not found
    @Override
    public void removeEmployeeById(Long id) {
        // 1. Kitoroljuk magat az alkalmazottat a fo listabol
        Employee removed = employeeMap.remove(id);

        if (removed != null) {
            // 2. A TE LOGIKAD: Csak azokat a kollegakat keressuk meg,
            // akikkel a torolt ember tenylegesen egyutt dolgozott!
            for (Long coworkerId : removed.getCoworkers().keySet()) {

                // Kikapjuk a kollegat a memoriabol az ID alapjan (azonnali lekerdezes)
                Employee coworker = employeeMap.get(coworkerId);

                // Es kitoroljuk nala a lapatra tett embert
                if (coworker != null) {
                    coworker.removeCoworker(id);
                }
            }
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Employee emp : employeeMap.values()) {
                String type = (emp instanceof DataAnalyst) ? "DA" : "SS";

                StringBuilder sb = new StringBuilder();
                // 1. VALTOZTATAS: Az ID kerul elore, ahogy az SQL-ben is lesz!
                sb.append(emp.getId()).append(";")
                        .append(type).append(";")
                        .append(emp.getFirstName()).append(";")
                        .append(emp.getLastName()).append(";")
                        .append(emp.getBirthYear()).append(";");

                List<String> coworkersList = new ArrayList<>();

                for (Map.Entry<Long, CollaborationLevel> entry : emp.getCoworkers().entrySet()) {
                    coworkersList.add(entry.getKey() + "=" + entry.getValue().name());
                }
                sb.append(String.join(",", coworkersList));

                writer.println(sb.toString());
            }
        } catch (Exception e) {
            System.out.println("X Chyba pri ukladani do souboru: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue;

                // 1. VALTOZTATAS: Az ID az elso (0. index), a Tipus a masodik (1. index)
                Long id = Long.parseLong(parts[0]);
                String type = parts[1];
                String firstName = parts[2];
                String lastName = parts[3];
                int birthYear = Integer.parseInt(parts[4]);

                Employee emp;
                if (type.equals("DA")) {
                    emp = new DataAnalyst(id, firstName, lastName, birthYear);
                } else {
                    emp = new SecuritySpecialist(id, firstName, lastName, birthYear);
                }

                String coworkersString = parts[5];
                if (!coworkersString.isEmpty()) {
                    String[] cwPairs = coworkersString.split(",");
                    for (String pair : cwPairs) {

                        String[] kv = pair.split("=");

                        Long cwId = Long.parseLong(kv[0]);
                        CollaborationLevel level = CollaborationLevel.valueOf(kv[1]);

                        emp.addCoworker(cwId, level);
                    }
                }

                this.addEmployee(emp);
            }
        } catch (Exception e) {
            System.out.println("X Chyba pri nacitani ze souboru: " + e.getMessage());
        }
    }

    @Override
    public List<Employee> getEmployeesByTypeSorted(String type) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employeeMap.values()) {
            if (type.equalsIgnoreCase("DA") && emp instanceof DataAnalyst) {
                filtered.add(emp);
            } else if (type.equalsIgnoreCase("SS") && emp instanceof SecuritySpecialist) {
                filtered.add(emp);
            }
        }

        // Itt hívjuk meg az automatikus rendezést, amit az Employee-ban megírtál!
        filtered.sort(null);
        return filtered;
    }

    //TODO: el kel felzni mivel egy konction 2 embrenkl szerepel
    // 1. A leggyakoribb minoseg (QoS)
    @Override
    public String getMostFrequentCollaborationLevel() {
        // Egy sima szamlalo Map: "EXCELLENT" -> 5 db, "POOR" -> 2 db
        Map<CollaborationLevel, Integer> stats = new HashMap<>();

        // Vegigmegyunk az osszes emberen
        for (Employee emp : employeeMap.values()) {
            // Vegigmegyunk a sajat kapcsolataikon
            for (CollaborationLevel level : emp.getCoworkers().values()) {
                // Ha mar benne van, adunk hozza 1-et, ha nincs, beirjuk 1-el
                stats.put(level, stats.getOrDefault(level, 0) + 1);
            }
        }

        if (stats.isEmpty()) {
            return "Zadne data";
        }

        // Most megkeressuk, melyik szam a legnagyobb
        CollaborationLevel mostFrequent = null;
        int maxCount = -1;

        for (Map.Entry<CollaborationLevel, Integer> entry : stats.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        return mostFrequent.name() + " (" + maxCount + "x)";
    }

    //TODO ha van tobb olyan ember, akinek ugyanannyi kapcsolata van, akkor az elsot adja vissza, amit megtalal. Ez egy egyszeru megoldas, de lehetne fejleszteni, hogy visszaadjon egy listat, vagy valami mas logika szerint dontson.
    // 2. A Core Switch (Legtobb kapcsolat)
    @Override
    public Employee getEmployeeWithMostConnections() {
        Employee topEmployee = null;
        int maxConnections = -1;

        // Vegigmegyunk mindenkin, es megnezzuk a "size()" fuggvenyt
        for (Employee emp : employeeMap.values()) {
            int currentConnections = emp.getCoworkers().size();

            if (currentConnections > maxConnections) {
                maxConnections = currentConnections;
                topEmployee = emp;
            }
        }

        return topEmployee;
    }

    @Override
    public Map<String, Integer> getEmployeeCountsByGroup() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("DA", 0);
        counts.put("SS", 0);

        // Egyetlen O(N) futas a memoriaban, tiszta szamolas
        for (Employee emp : employeeMap.values()) {
            if (emp instanceof DataAnalyst) {
                counts.put("DA", counts.get("DA") + 1);
            } else if (emp instanceof SecuritySpecialist) {
                counts.put("SS", counts.get("SS") + 1);
            }
        }
        return counts;
    }
}
