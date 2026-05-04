package org.example.employee.data;

import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqliteEmployeeStorageTest {

    private static final String TEST_DB = "test_employees.db";
    private SqliteEmployeeStorage storage;

    @BeforeEach
    void setUp() {

        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        storage = new SqliteEmployeeStorage(TEST_DB);
    }

    @AfterEach
    void tearDown() {

        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmployees() throws StorageException {

        Employee e1 = new DataAnalyst(1L, "Jan", "Novak", 1990);
        Employee e2 = new SecuritySpecialist(2L, "Petr", "Svoboda", 1985);

        e1.addCoworker(2L, CollaborationLevel.GOOD);
        e2.addCoworker(1L, CollaborationLevel.GOOD);

        List<Employee> toSave = Arrays.asList(e1, e2);


        storage.saveAll(toSave);
        List<Employee> loaded = storage.loadAll();


        assertEquals(2, loaded.size());

        Employee loaded1 = loaded.stream().filter(e -> e.getId() == 1L).findFirst().orElse(null);
        assertNotNull(loaded1);
        assertEquals("Jan", loaded1.getFirstName());
        assertEquals("DA", loaded1.getGroupId());
        assertTrue(loaded1.getCoworkers().containsKey(2L));
        assertEquals(CollaborationLevel.GOOD, loaded1.getCoworkers().get(2L));

        Employee loaded2 = loaded.stream().filter(e -> e.getId() == 2L).findFirst().orElse(null);
        assertNotNull(loaded2);
        assertEquals("Petr", loaded2.getFirstName());
        assertEquals("SS", loaded2.getGroupId());
    }

    @Test
    void testLoadEmptyDatabase() throws StorageException {

        List<Employee> loaded = storage.loadAll();


        assertTrue(loaded.isEmpty());
    }

    @Test
    void testUpdateExistingEmployees() throws StorageException {

        Employee e1 = new DataAnalyst(1L, "Jan", "Novak", 1990);
        storage.saveAll(List.of(e1));


        Employee e1Updated = new DataAnalyst(1L, "Jan", "Novotny", 1990);
        storage.saveAll(List.of(e1Updated));


        List<Employee> loaded = storage.loadAll();


        assertEquals(1, loaded.size());
        assertEquals("Novotny", loaded.getFirst().getLastName());
    }
}
