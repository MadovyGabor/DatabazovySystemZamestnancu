package org.example.employee.data;

import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for verifying the logic of {@link InMemoryEmployeeRepository}.
 * Ensures accurate tracking of IDs, relationships, and queries in memory.
 */
class InMemoryEmployeeRepositoryTest {

    @Test
    void getNextId_incrementsSequentially() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        assertEquals(1L, repository.getNextId());
        assertEquals(2L, repository.getNextId());
    }

    @Test
    void addEmployee_andGetEmployeeById_work() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        Employee employee = new DataAnalyst(1L, "Anna", "Nova", 1990);

        repository.addEmployee(employee);

        assertEquals(employee, repository.getEmployeeById(1L));
    }

    @Test
    void addEmployee_throwsOnDuplicateId() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        repository.addEmployee(new DataAnalyst(1L, "Anna", "Nova", 1990));

        assertThrows(BusinessException.class,
                () -> repository.addEmployee(new SecuritySpecialist(1L, "Petr", "Blue", 1988)));
    }

    @Test
    void removeEmployeeById_removesCoworkerLinks() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        DataAnalyst first = new DataAnalyst(1L, "Anna", "Nova", 1990);
        SecuritySpecialist second = new SecuritySpecialist(2L, "Petr", "Blue", 1988);
        first.addCoworker(2L, CollaborationLevel.GOOD);
        second.addCoworker(1L, CollaborationLevel.GOOD);
        repository.addEmployee(first);
        repository.addEmployee(second);

        repository.removeEmployeeById(1L);

        assertNull(repository.getEmployeeById(1L));
        assertFalse(repository.getEmployeeById(2L).getCoworkers().containsKey(1L));
    }

    @Test
    void loadAll_replacesExistingData() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        repository.addEmployee(new DataAnalyst(1L, "Old", "Data", 1990));

        repository.loadAll(List.of(new SecuritySpecialist(5L, "New", "Data", 1985)));

        assertNull(repository.getEmployeeById(1L));
        assertNotNull(repository.getEmployeeById(5L));
        assertEquals(6L, repository.getNextId());
    }

    @Test
    void aggregateQueries_returnExpectedValues() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        DataAnalyst first = new DataAnalyst(1L, "Anna", "Nova", 1990);
        SecuritySpecialist second = new SecuritySpecialist(2L, "Petr", "Blue", 1988);
        first.addCoworker(2L, CollaborationLevel.GOOD);
        second.addCoworker(1L, CollaborationLevel.GOOD);
        repository.addEmployee(first);
        repository.addEmployee(second);

        assertEquals("GOOD (2x)", repository.getMostFrequentCollaborationLevel());
        assertEquals(second, repository.getEmployeeWithMostConnections());
        Map<String, Integer> counts = repository.getEmployeeCountsByGroup();
        assertEquals(1, counts.get("DA"));
        assertEquals(1, counts.get("SS"));
    }

    // Tie rule: on equal counts the worse level wins (BAD, then AVERAGE, then GOOD).
    // A tie-break that prefers the better level (or takes the last maximum) returns GOOD and fails these tests.

    @Test
    void getMostFrequentCollaborationLevel_tieBetweenBadAndGood_returnsBad() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        addLinkedPair(repository, 1L, 2L, CollaborationLevel.GOOD);
        addLinkedPair(repository, 3L, 4L, CollaborationLevel.BAD);

        assertEquals("BAD (2x)", repository.getMostFrequentCollaborationLevel());
    }

    @Test
    void getMostFrequentCollaborationLevel_tieBetweenAverageAndGood_returnsAverage() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        addLinkedPair(repository, 1L, 2L, CollaborationLevel.GOOD);
        addLinkedPair(repository, 3L, 4L, CollaborationLevel.AVERAGE);

        assertEquals("AVERAGE (2x)", repository.getMostFrequentCollaborationLevel());
    }

    private static void addLinkedPair(InMemoryEmployeeRepository repository, long idA, long idB,
                                      CollaborationLevel level) {
        Employee a = new DataAnalyst(idA, "Name" + idA, "Test", 1990);
        Employee b = new SecuritySpecialist(idB, "Name" + idB, "Test", 1990);
        a.addCoworker(idB, level);
        b.addCoworker(idA, level);
        repository.addEmployee(a);
        repository.addEmployee(b);
    }
}
