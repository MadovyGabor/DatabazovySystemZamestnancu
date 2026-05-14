package org.example.employee.data;

import org.example.employee.domain.exceptions.StorageError;
import org.example.employee.domain.exceptions.StorageException;
import org.example.employee.domain.model.CollaborationLevel;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.employeeType.dataAnalyst.DataAnalyst;
import org.example.employee.domain.model.employeeType.securitySpecialist.SecuritySpecialist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for verifying the functionality of {@link FileEmployeeStorage}.
 * Checks file saving, loading, and error handling for various scenarios.
 */
class FileEmployeeStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAll_writesExpectedFormat() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());
        DataAnalyst employee = new DataAnalyst(1L, "Anna", "Nova", 1990);
        employee.addCoworker(2L, CollaborationLevel.GOOD);

        storage.saveAll(List.of(employee));

        String content = Files.readString(file).trim();
        assertEquals("1;DA;Anna;Nova;1990;2=GOOD", content);
    }

    @Test
    void saveAll_withEmptyCollection_createsEmptyFile() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        storage.saveAll(List.of());

        assertEquals("", Files.readString(file));
    }

    @Test
    void loadAll_readsEmployeesAndCoworkers() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "1;DA;Anna;Nova;1990;2=GOOD\n2;SS;Petr;Blue;1988;1=GOOD\n");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        List<Employee> employees = storage.loadAll();

        assertEquals(2, employees.size());
        assertEquals("Anna", employees.get(0).getFirstName());
        assertEquals(CollaborationLevel.GOOD, employees.get(0).getCoworkers().get(2L));
        assertEquals("Petr", employees.get(1).getFirstName());
        assertEquals(CollaborationLevel.GOOD, employees.get(1).getCoworkers().get(1L));
    }

    @Test
    void loadAll_skipsBlankLines() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "1;DA;Anna;Nova;1990;\n\n2;SS;Petr;Blue;1988;\n");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        List<Employee> employees = storage.loadAll();

        assertEquals(2, employees.size());
    }

    @Test
    void loadAll_throwsWhenFileMissing() {
        FileEmployeeStorage storage = new FileEmployeeStorage(tempDir.resolve("missing.txt").toString());

        StorageException ex = assertThrows(StorageException.class, storage::loadAll);

        assertEquals(StorageError.FILE_NOT_FOUND, ex.getError());
    }

    @Test
    void loadAll_throwsWhenContentCorrupted() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "1;DA;Anna;Nova;1990;broken-pair\n");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        StorageException ex = assertThrows(StorageException.class, storage::loadAll);

        assertEquals(StorageError.DATA_CORRUPTED, ex.getError());
    }

    @Test
    void loadAll_throwsWhenTypeIsUnknown() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "1;XX;Anna;Nova;1990;\n");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        StorageException ex = assertThrows(StorageException.class, storage::loadAll);

        assertEquals(StorageError.DATA_CORRUPTED, ex.getError());
    }

    @Test
    void loadAll_throwsWhenCoworkerLevelIsInvalid() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "1;DA;Anna;Nova;1990;2=WRONG\n");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        StorageException ex = assertThrows(StorageException.class, storage::loadAll);

        assertEquals(StorageError.DATA_CORRUPTED, ex.getError());
    }

    @Test
    void saveAll_overwritesExistingFile() throws Exception {
        Path file = tempDir.resolve("employees.txt");
        Files.writeString(file, "old content");
        FileEmployeeStorage storage = new FileEmployeeStorage(file.toString());

        storage.saveAll(List.of(new SecuritySpecialist(2L, "Petr", "Blue", 1988)));

        String content = Files.readString(file).trim();
        assertEquals("2;SS;Petr;Blue;1988;", content);
    }
}
