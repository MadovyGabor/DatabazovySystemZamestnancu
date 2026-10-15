package org.example.employee.domain;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.exceptions.StorageError;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for verifying the business logic in {@link EmployeeService}.
 * Tests operations like adding employees, collaborations, and exception throwing.
 */
class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(new InMemoryEmployeeRepository(), null, null);
    }

    @Test
    void addEmployee_savesEmployee() {
        service.addEmployee(new DataAnalyst(1L, "Test", "User", 1990));

        assertEquals("Test", service.getEmployeeById(1L).getFirstName());
    }

    @Test
    void addEmployee_throwsWhenDuplicateIdExists() {
        service.addEmployee(new DataAnalyst(1L, "First", "User", 1990));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.addEmployee(new SecuritySpecialist(1L, "Second", "User", 1991)));

        assertEquals(BusinessError.DUPLICATE_ID, ex.getError());
    }

    @Test
    void getEmployeeById_throwsWhenMissing() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.getEmployeeById(99L));

        assertEquals(BusinessError.EMPLOYEE_NOT_FOUND, ex.getError());
    }

    @Test
    void addCollaboration_throwsWhenSelfCollaboration() {
        service.addEmployee(new DataAnalyst(1L, "Sanyi", "Vagyok", 1986));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.addCollaboration(1L, 1L, CollaborationLevel.GOOD));

        assertEquals(BusinessError.SELF_COLLABORATION, ex.getError());
    }

    @Test
    void addCollaboration_linksBothEmployees() {
        service.addEmployee(new DataAnalyst(1L, "Anna", "Nova", 1990));
        service.addEmployee(new SecuritySpecialist(2L, "Petr", "Blue", 1988));

        service.addCollaboration(1L, 2L, CollaborationLevel.AVERAGE);

        assertEquals(CollaborationLevel.AVERAGE, service.getEmployeeById(1L).getCoworkers().get(2L));
        assertEquals(CollaborationLevel.AVERAGE, service.getEmployeeById(2L).getCoworkers().get(1L));
    }

    @Test
    void saveToBackup_doesNotOverwriteBackupWhenLoadFailed() {
        FakeBackupStorage backup = new FakeBackupStorage(true);
        EmployeeService failingService = new EmployeeService(new InMemoryEmployeeRepository(), null, backup);
        assertThrows(StorageException.class, failingService::loadFromBackup);
        failingService.addEmployee(new DataAnalyst(1L, "Anna", "Nova", 1990));

        StorageException ex = assertThrows(StorageException.class, failingService::saveToBackup);

        assertEquals(StorageError.BACKUP_NOT_LOADED, ex.getError());
        assertFalse(backup.saveCalled);
    }

    @Test
    void saveToBackup_writesBackupWhenLoadSucceeded() {
        FakeBackupStorage backup = new FakeBackupStorage(false);
        EmployeeService okService = new EmployeeService(new InMemoryEmployeeRepository(), null, backup);
        okService.loadFromBackup();
        okService.addEmployee(new DataAnalyst(1L, "Anna", "Nova", 1990));

        okService.saveToBackup();

        assertTrue(backup.saveCalled);
    }

    /** Backup storage double: optionally fails on load, records whether save was called. */
    private static final class FakeBackupStorage implements EmployeeStorage {
        private final boolean failOnLoad;
        private boolean saveCalled = false;

        FakeBackupStorage(boolean failOnLoad) {
            this.failOnLoad = failOnLoad;
        }

        @Override
        public void saveAll(Collection<Employee> employees) {
            saveCalled = true;
        }

        @Override
        public List<Employee> loadAll() throws StorageException {
            if (failOnLoad) {
                throw new StorageException(StorageError.PERSISTENCE_ERROR, "test double");
            }
            return List.of();
        }
    }
}
