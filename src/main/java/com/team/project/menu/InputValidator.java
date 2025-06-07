package com.team.project.menu;

import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

public class InputValidator {

    public static String getNonEmptyString(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    public static int getValidInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public static Date getValidDate(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Please enter in yyyy-mm-dd format.");
            }
        }
    }

    public static Time getValidTime(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Time.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid time format. Please enter in HH:mm:ss format.");
            }
        }
    }

    public static boolean getValidBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (true/false): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            }
            System.out.println("Please enter either 'true' or 'false'.");
        }
    }
}
