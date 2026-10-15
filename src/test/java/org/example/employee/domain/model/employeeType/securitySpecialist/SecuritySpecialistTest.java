package org.example.employee.domain.model.employeeType.securitySpecialist;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecuritySpecialistTest {

    @Test
    void executeSkill_equalRisks_orderedByAscendingId() {
        // Both targets have risk 0.0 (three GOOD links each). Ids 17 and 1 share one HashMap
        // bucket and are iterated in insertion order (17 first), so a sort by risk alone keeps 17 first.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst high = new DataAnalyst(17L, "Anna", "Nova", 1990);
        DataAnalyst low = new DataAnalyst(1L, "Jan", "Novak", 1991);
        DataAnalyst extraA = new DataAnalyst(50L, "Eva", "Dvorak", 1985);
        DataAnalyst extraB = new DataAnalyst(51L, "Ota", "Horak", 1986);
        for (Employee e : new Employee[] {specialist, high, low, extraA, extraB}) {
            repository.addEmployee(e);
        }
        link(specialist, high, CollaborationLevel.GOOD);
        link(specialist, low, CollaborationLevel.GOOD);
        for (Employee target : new Employee[] {high, low}) {
            link(target, extraA, CollaborationLevel.GOOD);
            link(target, extraB, CollaborationLevel.GOOD);
        }

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(2, result.connectionRisks().size());
        assertEquals(Long.valueOf(1L), result.connectionRisks().get(0).coworkerId());
        assertEquals(Long.valueOf(17L), result.connectionRisks().get(1).coworkerId());
    }

    /** Links two employees symmetrically with the same collaboration level. */
    private static void link(Employee a, Employee b, CollaborationLevel level) {
        a.addCoworker(b.getId(), level);
        b.addCoworker(a.getId(), level);
    }
}
