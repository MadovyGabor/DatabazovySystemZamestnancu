package org.example;

import org.example.employee.data.InMemoryEmployeeRepository;
import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.EmployeeService;
import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.model.employeeType.DataAnalyst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic unit tests for EmployeeService business rules.
 */
class EmployeeServiceTest {

    private EmployeeService service;
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmployeeRepository();
        service = new EmployeeService(repository, null);
    }

    @Test
    void testAddEmployee_ShouldSaveSuccessfully() {
        DataAnalyst da = new DataAnalyst(1L, "Test", "User", 1990);
        service.addEmployee(da);
        assertNotNull(service.getEmployeeById(1L));
        assertEquals("Test", service.getEmployeeById(1L).getFirstName());
    }

    @Test
    void testAddCollaboration_ShouldThrowException_WhenSelfCollaboration() {
        service.addEmployee(new DataAnalyst(1L, "Sanyi", "Vagyok", 1986));
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.addCollaboration(1L, 1L, null);
        });

        assertEquals(BusinessError.SELF_COLLABORATION, ex.getError());
    }
}