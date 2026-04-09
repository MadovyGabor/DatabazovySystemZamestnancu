package org.example.employee.domain;


import java.util.HashMap;
import java.util.Map;

public abstract class Employee implements Comparable<Employee>{

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int birthYear;

    private Map<Long, CollaborationLevel> coworkers;

    // Egyetlen, tiszta konstruktor
    public Employee(Long id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.coworkers = new HashMap<>();
    }

    // --- GETTEREK (Mindenhez van, hogy ki tudjuk olvasni az adatokat) ---
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public Map<Long, CollaborationLevel> getCoworkers() {
        return coworkers;
    }

    // --- EGYEDI METÓDUSOK (Setterek nincsenek az alap adatokhoz!) ---

    // Munkatárs hozzáadása / frissítése
    public void addCoworker(Long coworkerId, CollaborationLevel level) {
        this.coworkers.put(coworkerId, level);
    }

    // Munkatárs törlése
    public void removeCoworker(Long coworkerId) {
        this.coworkers.remove(coworkerId);
    }

    // Az absztrakt metódus, amit a csoportok fognak megvalósítani
    public abstract void executeSkill();

    @Override
    public int compareTo(Employee other) {
        // Alapértelmezett rendezés: vezetéknév, majd keresztnév (ABC)
        int res = this.lastName.compareToIgnoreCase(other.lastName);
        if (res == 0) {
            return this.firstName.compareToIgnoreCase(other.firstName);
        }
        return res;
    }
}