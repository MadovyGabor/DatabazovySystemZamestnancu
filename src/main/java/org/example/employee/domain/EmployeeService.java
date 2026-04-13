package org.example.employee.domain;

import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;

import java.util.Collection;


/**
 * Service layer exposing operations for managing employees, collaborations and persistence.
 * Performs business validations and delegates storage/repository concerns.
 */
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeStorage storage;

    public EmployeeService(EmployeeRepository repository, EmployeeStorage storage) {
        this.repository = repository;
        this.storage = storage;
    }

    /**
     * Returns the next available id from the repository.
     */
    public Long getNextId() {
        return repository.getNextId();
    }

    /**
     * Add a new employee after validating there is no duplicate id.
     *
     * @throws BusinessException when an employee with the same id already exists
     */
    public void addEmployee(Employee employee) {
        if (repository.getEmployeeById(employee.getId()) != null) {
            throw new BusinessException(BusinessError.DUPLICATE_ID);
        }
        repository.addEmployee(employee);
    }

    /**
     * Retrieve an employee by id or throw a BusinessException if not found.
     */
    public Employee getEmployeeById(Long id) {
        Employee employee = repository.getEmployeeById(id);
        if (employee == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }
        return employee;
    }

    /**
     * Remove an employee (validates existence first).
     */
    public void removeEmployeeById(Long id) {
        getEmployeeById(id);
        repository.removeEmployeeById(id);
    }

    /**
     * Return all employees from the repository.
     */
    public Collection<Employee> getAllEmployees() {
        return repository.getAllEmployees();
    }

    /**
     * Add a bidirectional collaboration link between two employees with a given level.
     * Validates that the ids are different and the collaboration does not already exist.
     */
    public void addCollaboration(Long employeeId, Long coworkerId, CollaborationLevel level) {
        if (employeeId.equals(coworkerId)) {
            throw new BusinessException(BusinessError.SELF_COLLABORATION);
        }

        Employee employee = getEmployeeById(employeeId);
        Employee coworker = getEmployeeById(coworkerId);

        if (employee.getCoworkers().containsKey(coworkerId)) {
            throw new BusinessException(BusinessError.COLLABORATION_ALREADY_EXISTS);
        }

        employee.addCoworker(coworkerId, level);
        coworker.addCoworker(employeeId, level);
    }

    /**
     * Persist current repository data using the configured storage implementation.
     */
    public void saveData() throws StorageException {
        storage.saveAll(repository.getAllEmployees());
    }

    /**
     * Load data from storage into the repository.
     */
    public void loadData() throws StorageException {
        repository.loadAll(storage.loadAll());
    }
    public java.util.List<Employee> getEmployeesByTypeSorted(String type) {
        return repository.getEmployeesByTypeSorted(type);
    }

    public String getMostFrequentCollaborationLevel() {
        return repository.getMostFrequentCollaborationLevel();
    }

    public Employee getEmployeeWithMostConnections() {
        return repository.getEmployeeWithMostConnections();
    }

    public java.util.Map<String, Integer> getEmployeeCountsByGroup() {
        return repository.getEmployeeCountsByGroup();
    }
}
