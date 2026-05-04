package org.example.employee.domain;

import org.example.employee.domain.exceptions.BusinessError;
import org.example.employee.domain.exceptions.BusinessException;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.Collection;


public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeStorage storage;
    private final EmployeeStorage backupStorage;

    public EmployeeService(EmployeeRepository repository, EmployeeStorage storage, EmployeeStorage backupStorage) {
        this.repository = repository;
        this.storage = storage;
        this.backupStorage = backupStorage;
    }


    public Long getNextId() {
        return repository.getNextId();
    }


    public void addEmployee(Employee employee) {
        if (repository.getEmployeeById(employee.getId()) != null) {
            throw new BusinessException(BusinessError.DUPLICATE_ID);
        }
        repository.addEmployee(employee);
    }


    public Employee getEmployeeById(Long id) {
        Employee employee = repository.getEmployeeById(id);
        if (employee == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }
        return employee;
    }


    public void removeEmployeeById(Long id) {
        getEmployeeById(id);
        repository.removeEmployeeById(id);
    }

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


    public void saveData() throws StorageException {
        storage.saveAll(repository.getAllEmployees());
    }


    public void loadData() throws StorageException {
        repository.loadAll(storage.loadAll());
    }


    public void saveToBackup() throws StorageException {
        if (backupStorage != null) {
            backupStorage.saveAll(repository.getAllEmployees());
        }
    }


    public void loadFromBackup() throws StorageException {
        if (backupStorage != null) {
            repository.loadAll(backupStorage.loadAll());
        }
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

    public EmployeeTaskResults executeEmployeeSkill(Long employeeId) {
        Employee employee = repository.getEmployeeById(employeeId);
        if (employee == null) {
            throw new BusinessException(BusinessError.EMPLOYEE_NOT_FOUND);
        }

        return employee.executeSkill(repository);
    }
}
