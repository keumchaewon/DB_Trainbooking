package com.team.project.menu;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public static Date getValidDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input);
                return Date.valueOf(date);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format or value. Please enter in yyyy-mm-dd format.");
            }
        }
    }

    public static Time getValidTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                LocalTime time = LocalTime.parse(input, DateTimeFormatter.ofPattern("H:mm:ss"));
                return Time.valueOf(time);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time. Format must be HH:mm:ss with valid range (00–24:00:00).");
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

    public static String getValidTrainName(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.matches("KTX-\\d{3}")) {
                return input;
            }
            System.out.println("Invalid format. Train name must be in the format 'KTX-XXX' (e.g., KTX-100).");
        }
    }

    public static String getValidTrainType(Scanner scanner, String prompt) {
        String[] validTypes = {"급행", "고속", "완행"};
        while (true) {
            System.out.print(prompt + " (급행/고속/완행): ");
            String input = scanner.nextLine().trim();
            for (String type : validTypes) {
                if (input.equals(type)) return input;
            }
            System.out.println("Invalid train type. Please enter '급행', '고속', or '완행'.");
        }
    }

    public static String getValidSeatNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.matches("\\d+[A-Z]")) {
                return input;
            }
            System.out.println("Invalid seat number. Format should be like '1A', '10B', etc.");
        }
    }

    // [추가] 유효한 행 개수 입력 받기
    public static int getValidRowCount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0 && value <= 1000) return value;
                System.out.println("Please enter a positive integer less than or equal to 1000.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    // [추가] 유효한 열 문자 입력 받기 (A~Z)
    public static String getValidColumn(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.matches("^[A-Z]$")) {
                return input;
            }
            System.out.println("Invalid input. Please enter a single uppercase letter from A to Z.");
        }
    }
}
