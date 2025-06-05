package com.team.project;

import com.team.project.menu.InsertMenu;
import com.team.project.menu.SelectMenu;
import com.team.project.menu.DeleteMenu;
import com.team.project.menu.UpdateMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                // Main Menu
                System.out.println("\n=== Train Reservation System ===");
                System.out.println("1. Customer Menu");
                System.out.println("2. Staff Menu");
                System.out.println("0. Exit");
                System.out.print("Please select a menu: ");
                int choice = sc.nextInt();
                sc.nextLine();  // Clear buffer

                switch (choice) {
                    case 1 -> customerMenu(sc);       // Customer Menu
                    case 2 -> staffMenu(sc);          // Staff Menu
                    case 0 -> {
                        System.out.println("Exiting the program.");
                        return;  // Exit the program
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }

    // Customer Menu
    private static void customerMenu(Scanner sc) {
        System.out.println("\n[ Customer Menu ]");
        System.out.println("1. Train Booking / Inquiry");
        System.out.println("2. Reservation Management");
        System.out.println("3. User Management");
        System.out.println("0. Back to Main Menu");
        System.out.print("Select option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> trainBookingInquiryMenu(sc);   // Train Booking / Inquiry
            case 2 -> reservationManagementMenu(sc);  // Reservation Management
            case 3 -> userManagementMenu(sc);         // User Management
            case 0 -> System.out.println("Returning to Main Menu.");
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    // Train Booking / Inquiry Menu
    private static void trainBookingInquiryMenu(Scanner sc) {
        System.out.println("\n[ Train Booking / Inquiry ]");
        System.out.println("1. Book Train Ticket");
        System.out.println("2. View Available Trains");
        System.out.println("3. View Reservation Details");
        System.out.println("4. Confirm Train Ticket Booking");
        System.out.println("0. Back to Customer Menu");
        System.out.print("Select option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> InsertMenu.BookingMenu.run(sc);                  // Book Train Ticket -> insert인듯
            case 2 -> SelectMenu.showAvailableTrains(sc);    // View Available Trains
            case 3 -> SelectMenu.showReservations(sc);       // View Reservation Details
            case 4 -> SelectMenu.confirmReservation(sc);     // Confirm Train Ticket Booking
            case 0 -> customerMenu(sc);                      // Back to Customer Menu
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    // Reservation Management Menu
    private static void reservationManagementMenu(Scanner sc) {
        System.out.println("\n[ Reservation Management ]");
        System.out.println("1. View Reservation");
        System.out.println("2. Modify Reservation");
        System.out.println("3. Cancel Reservation");
        System.out.println("0. Back to Customer Menu");
        System.out.print("Select option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> SelectMenu.viewReservation(sc);   // View Reservation
            case 2 -> UpdateMenu.modifyReservation(sc);  // Modify Reservation
            case 3 -> DeleteMenu.run(sc);                     // Cancel Reservation
            case 0 -> customerMenu(sc);                       // Back to Customer Menu
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    // User Management Menu
    private static void userManagementMenu(Scanner sc) {
        System.out.println("\n[ User Management ]");
        System.out.println("1. Register User");
        System.out.println("2. Edit User Information");
        System.out.println("3. Delete User");
        System.out.println("0. Back to Customer Menu");
        System.out.print("Select option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1" -> InsertMenu.registerUser(sc);          // Register User
            case "2" -> UpdateMenu.updateUserInfo(sc);        // Edit User Information
            case "3" -> DeleteMenu.deleteUser(sc);            // Delete User
            case "0" -> customerMenu(sc);                   // Back to Customer Menu
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    // Staff Menu
    private static void staffMenu(Scanner sc) {
        System.out.println("\n=== Staff Menu ===");
        System.out.println("1. Register Train");
        System.out.println("2. Register Route");
        System.out.println("3. Register Schedule");
        System.out.println("4. Register Seat");
        System.out.println("5. Register Reservation");
        System.out.println("6. View Train Information");
        System.out.println("0. Back to Main Menu");
        System.out.print("Select option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> InsertMenu.insertTrain(sc);        // Register Train
            case 2 -> InsertMenu.insertRoute(sc);        // Register Route
            case 3 -> InsertMenu.insertSchedule(sc);     // Register Schedule
            case 4 -> InsertMenu.insertSeat(sc);         // Register Seat
            case 5 -> InsertMenu.insertReservation(sc);  // Register Reservation
            case 6 -> SelectMenu.showTrainInfo(sc);      // View Train Information
            case 0 -> System.out.println("Returning to Main Menu.");
            default -> System.out.println("Invalid option, please try again.");
        }
    }
}
