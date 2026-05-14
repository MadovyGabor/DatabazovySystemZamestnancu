package org.example.employee.domain;

import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.EmployeeTaskResults;

/**
 * Service class that handles business logic for managing employees.
 * Orchestrates operations between the repository and storage mechanisms.
 */
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeStorage storage;
    private final EmployeeStorage backupStorage;

    /**
     * Constructs a new EmployeeService.
     *
     * @param repository    The primary repository for employee data.
     * @param storage       The primary storage mechanism (e.g., file).
     * @param backupStorage The secondary backup storage mechanism (e.g., database).
     */
    public EmployeeService(EmployeeRepository repository, EmployeeStorage storage, EmployeeStorage backupStorage) {
        this.repository = repository;
        this.storage = storage;
        this.backupStorage = backupStorage;
    }

    /**
     * Retrieves the next available ID from the repository.
     *
     * @return The next ID.
     */
    public Long getNextId() {
        return repository.getNextId();
    }

    /**
     * Adds an employee, ensuring no duplicate IDs exist.
     *
     * @param employee The employee to add.
     * @throws BusinessException If a duplicate ID is found.
     */
    public void addEmployee(Employee employee) {
        if (repository.getEmployeeById(employee.getId()) != null) {
            throw new BusinessException(BusinessError.DUPLICATE_ID);
        }
        repository.addEmployee(employee);
    }

    /**
     * Retrieves an employee by ID.
     *
     * @param id The ID to find.
     * @return The found employee.
     * @throws BusinessException If the employee is not found.
     */
    public Employee getEmployeeById(Long id) {
        Employee employee = repository.getEmployeeById(id);
        if (employee == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }
        return employee;
    }

    /**
     * Removes an employee by ID. First checks if they exist.
     *
     * @param id The ID of the employee to remove.
     * @throws BusinessException If the employee is not found.
     */
    public void removeEmployeeById(Long id) {
        getEmployeeById(id);
        repository.removeEmployeeById(id);
    }

    /**
     * Adds a mutual collaboration link between two employees.
     *
     * @param employeeId The ID of the first employee.
     * @param coworkerId The ID of the second employee.
     * @param level      The level of collaboration.
     * @throws BusinessException If an employee doesn't exist, it's a
     *                           self-collaboration, or it already exists.
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
     * Saves all current repository data to the primary storage.
     *
     * @throws StorageException If a persistence error occurs.
     */
    public void saveData() throws StorageException {
        storage.saveAll(repository.getAllEmployees());
    }

    /**
     * Loads all data from the primary storage into the repository.
     *
     * @throws StorageException If a persistence error occurs.
     */
    public void loadData() throws StorageException {
        repository.loadAll(storage.loadAll());
    }

    /**
     * Saves all current repository data to the backup storage, if configured.
     *
     * @throws StorageException If a persistence error occurs.
     */
    public void saveToBackup() throws StorageException {
        if (backupStorage != null) {
            backupStorage.saveAll(repository.getAllEmployees());
        }
    }

    /**
     * Loads all data from the backup storage into the repository, if configured.
     *
     * @throws StorageException If a persistence error occurs.
     */
    public void loadFromBackup() throws StorageException {
        if (backupStorage != null) {
            repository.loadAll(backupStorage.loadAll());
        }
    }

    /**
     * Retrieves all employees of a given type, sorted.
     *
     * @param type The group ID.
     * @return Sorted list of employees.
     */
    public java.util.List<Employee> getEmployeesByTypeSorted(String type) {
        return repository.getEmployeesByTypeSorted(type);
    }

    /**
     * Gets the most frequent collaboration level.
     *
     * @return String representation of the most frequent level.
     */
    public String getMostFrequentCollaborationLevel() {
        return repository.getMostFrequentCollaborationLevel();
    }

    /**
     * Gets the employee with the most connections.
     *
     * @return The most connected employee.
     */
    public Employee getEmployeeWithMostConnections() {
        return repository.getEmployeeWithMostConnections();
    }

    /**
     * Gets a map of employee counts grouped by their type.
     *
     * @return Map of group IDs to counts.
     */
    public java.util.Map<String, Integer> getEmployeeCountsByGroup() {
        return repository.getEmployeeCountsByGroup();
    }

    /**
     * Executes the specific skill associated with an employee.
     *
     * @param employeeId The ID of the employee whose skill to execute.
     * @return The results of the skill execution.
     * @throws BusinessException If the employee is not found.
     */
    public EmployeeTaskResults executeEmployeeSkill(Long employeeId) {
        Employee employee = repository.getEmployeeById(employeeId);
        if (employee == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }

        return employee.executeSkill(repository);
    }
}
