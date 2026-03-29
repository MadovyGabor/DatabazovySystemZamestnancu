package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        Scanner sr = new Scanner(System.in);
        System.out.print("Please enter your name: ");
        String name = sr.nextLine();
        System.out.println("Hello " + name + "!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}