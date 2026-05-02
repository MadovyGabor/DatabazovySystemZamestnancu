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
}
