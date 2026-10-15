package org.example.employee.presentation;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleViewUtilsTest {

    @Test
    void readName_rejectsDelimitersAndAsksAgain() {
        // The first three lines contain ';', ',' and '=' and must be rejected.
        Scanner scanner = new Scanner("Ann;Smith\nAnn,Smith\nAnn=Smith\nAnna\n");

        assertEquals("Anna", ConsoleViewUtils.readName(scanner, ""));
    }

    @Test
    void readName_acceptsPlainName() {
        Scanner scanner = new Scanner("Anna\n");

        assertEquals("Anna", ConsoleViewUtils.readName(scanner, ""));
    }
}
