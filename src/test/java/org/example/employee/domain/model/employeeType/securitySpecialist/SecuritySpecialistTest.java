package org.example.employee.domain.model.employeeType.securitySpecialist;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecuritySpecialistTest {

    @Test
    void executeSkill_withoutCoworkers_returnsZeroRisk() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        repository.addEmployee(specialist);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(0.0, result.totalRiskScore());
        assertTrue(result.connectionRisks().isEmpty());
    }

    @Test
    void executeSkill_targetWithoutLinks_hasRisk100() {
        // The specialist lists the target, but the target has no links at all.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst target = new DataAnalyst(10L, "Anna", "Nova", 1990);
        repository.addEmployee(specialist);
        repository.addEmployee(target);
        specialist.addCoworker(10L, CollaborationLevel.GOOD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(100.0, result.connectionRisks().get(0).score());
        assertEquals(100.0, result.totalRiskScore());
    }

    @Test
    void executeSkill_targetWithThreeGoodLinks_hasRisk0() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst target = new DataAnalyst(10L, "Anna", "Nova", 1990);
        DataAnalyst extraA = new DataAnalyst(60L, "Eva", "Dvorak", 1985);
        DataAnalyst extraB = new DataAnalyst(61L, "Ota", "Horak", 1986);
        for (Employee e : new Employee[] {specialist, target, extraA, extraB}) {
            repository.addEmployee(e);
        }
        link(specialist, target, CollaborationLevel.GOOD);
        link(target, extraA, CollaborationLevel.GOOD);
        link(target, extraB, CollaborationLevel.GOOD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(0.0, result.connectionRisks().get(0).score());
    }

    @Test
    void executeSkill_targetWithFewerThanThreeLinks_addsPenalty20() {
        // Two GOOD links (base 0) plus the isolation penalty of 20.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst target = new DataAnalyst(10L, "Anna", "Nova", 1990);
        DataAnalyst extra = new DataAnalyst(60L, "Eva", "Dvorak", 1985);
        for (Employee e : new Employee[] {specialist, target, extra}) {
            repository.addEmployee(e);
        }
        link(specialist, target, CollaborationLevel.GOOD);
        link(target, extra, CollaborationLevel.GOOD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(20.0, result.connectionRisks().get(0).score());
    }

    @Test
    void executeSkill_riskIsCappedAt100() {
        // One BAD link gives base 100; the penalty of 20 would make 120, so the result is capped.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst target = new DataAnalyst(10L, "Anna", "Nova", 1990);
        repository.addEmployee(specialist);
        repository.addEmployee(target);
        link(specialist, target, CollaborationLevel.BAD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(100.0, result.connectionRisks().get(0).score());
    }

    @Test
    void executeSkill_mixedLevels_averagesLinkValues() {
        // Links AVERAGE (50), GOOD (0) and BAD (100): mean 50.0, three links so no penalty.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst target = new DataAnalyst(10L, "Anna", "Nova", 1990);
        DataAnalyst extraA = new DataAnalyst(60L, "Eva", "Dvorak", 1985);
        DataAnalyst extraB = new DataAnalyst(61L, "Ota", "Horak", 1986);
        for (Employee e : new Employee[] {specialist, target, extraA, extraB}) {
            repository.addEmployee(e);
        }
        link(specialist, target, CollaborationLevel.AVERAGE);
        link(target, extraA, CollaborationLevel.GOOD);
        link(target, extraB, CollaborationLevel.BAD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(50.0, result.connectionRisks().get(0).score());
    }

    @Test
    void executeSkill_sortsByRiskDescending_andAveragesAllTargets() {
        // Targets: 10 has risk 0, 11 has risk 100 (capped), 12 has risk 20.
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        SecuritySpecialist specialist = new SecuritySpecialist(100L, "Petr", "Blue", 1988);
        DataAnalyst safe = new DataAnalyst(10L, "Anna", "Nova", 1990);
        DataAnalyst risky = new DataAnalyst(11L, "Jan", "Novak", 1991);
        DataAnalyst medium = new DataAnalyst(12L, "Eva", "Dvorak", 1985);
        DataAnalyst extraA = new DataAnalyst(60L, "Ota", "Horak", 1986);
        DataAnalyst extraB = new DataAnalyst(61L, "Lea", "Kral", 1987);
        for (Employee e : new Employee[] {specialist, safe, risky, medium, extraA, extraB}) {
            repository.addEmployee(e);
        }
        link(specialist, safe, CollaborationLevel.GOOD);
        link(safe, extraA, CollaborationLevel.GOOD);
        link(safe, extraB, CollaborationLevel.GOOD);
        link(specialist, risky, CollaborationLevel.BAD);
        link(specialist, medium, CollaborationLevel.GOOD);
        link(medium, extraA, CollaborationLevel.GOOD);

        SecurityResult result = (SecurityResult) specialist.executeSkill(repository);

        assertEquals(Long.valueOf(11L), result.connectionRisks().get(0).coworkerId());
        assertEquals(100.0, result.connectionRisks().get(0).score());
        assertEquals(Long.valueOf(12L), result.connectionRisks().get(1).coworkerId());
        assertEquals(20.0, result.connectionRisks().get(1).score());
        assertEquals(Long.valueOf(10L), result.connectionRisks().get(2).coworkerId());
        assertEquals(0.0, result.connectionRisks().get(2).score());
        assertEquals(40.0, result.totalRiskScore());
    }

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
