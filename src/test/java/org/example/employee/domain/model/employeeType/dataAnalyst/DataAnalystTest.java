package org.example.employee.domain.model.employeeType.dataAnalyst;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAnalystTest {

    @Test
    void executeSkill_withoutCoworkers_returnsEmptyResult() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        DataAnalyst analyst = new DataAnalyst(1L, "Anna", "Nova", 1990);
        repository.addEmployee(analyst);

        AnalystResult result = (AnalystResult) analyst.executeSkill(repository);

        assertNull(result.bestMatchId());
        assertNull(result.bestMatchName());
        assertEquals(0, result.commonCount());
    }

    @Test
    void executeSkill_uniqueBestMatch_returnsCoworkerWithMostCommonCoworkers() {
        // Analyst 1 knows 2, 3 and 4. Coworker 2 also knows 3 and 4 (2 common coworkers).
        // Coworkers 3 and 4 each share only coworker 2 (1 common coworker).
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        DataAnalyst analyst = new DataAnalyst(1L, "Anna", "Nova", 1990);
        DataAnalyst best = new DataAnalyst(2L, "Petr", "Blue", 1988);
        DataAnalyst third = new DataAnalyst(3L, "Jan", "Novak", 1991);
        DataAnalyst fourth = new DataAnalyst(4L, "Eva", "Dvorak", 1985);
        for (Employee e : new Employee[] {analyst, best, third, fourth}) {
            repository.addEmployee(e);
        }
        link(analyst, best, CollaborationLevel.GOOD);
        link(analyst, third, CollaborationLevel.GOOD);
        link(analyst, fourth, CollaborationLevel.GOOD);
        link(best, third, CollaborationLevel.GOOD);
        link(best, fourth, CollaborationLevel.GOOD);

        AnalystResult result = (AnalystResult) analyst.executeSkill(repository);

        assertEquals(Long.valueOf(2L), result.bestMatchId());
        assertEquals("Petr Blue", result.bestMatchName());
        assertEquals(2, result.commonCount());
    }

    @Test
    void executeSkill_tieOnCommonCount_returnsCoworkerWithHigherId() {
        // Analyst 1 knows 2 and 3; 2 and 3 know each other. Each has one common coworker,
        // so the tie must be decided by ID (3 > 2). Strict ">" alone keeps 2.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        DataAnalyst analyst = new DataAnalyst(1L, "Anna", "Nova", 1990);
        DataAnalyst second = new DataAnalyst(2L, "Petr", "Blue", 1988);
        DataAnalyst third = new DataAnalyst(3L, "Jan", "Novak", 1991);
        for (Employee e : new Employee[] {analyst, second, third}) {
            repository.addEmployee(e);
        }
        link(analyst, second, CollaborationLevel.GOOD);
        link(analyst, third, CollaborationLevel.GOOD);
        link(second, third, CollaborationLevel.GOOD);

        AnalystResult result = (AnalystResult) analyst.executeSkill(repository);

        assertEquals(Long.valueOf(3L), result.bestMatchId());
        assertEquals(1, result.commonCount());
    }

    /** Links two employees symmetrically with the same collaboration level. */
    static void link(Employee a, Employee b, CollaborationLevel level) {
        a.addCoworker(b.getId(), level);
        b.addCoworker(a.getId(), level);
    }
}
