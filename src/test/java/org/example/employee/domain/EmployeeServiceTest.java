package org.example.employee.domain;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(new InMemoryEmployeeRepository(), null);
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
}

