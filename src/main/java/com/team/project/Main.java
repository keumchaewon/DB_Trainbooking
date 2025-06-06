package com.team.project;

import com.team.project.menu.InsertMenu;
import com.team.project.menu.SelectMenu;
import com.team.project.menu.DeleteMenu;
import com.team.project.menu.UpdateMenu;

import java.util.Scanner;

public class Main {

    private static final String STAFF_PASSWORD = "nollback";  // 관리자 비밀번호

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
                    case 2 -> {
                        if (verifyStaffPassword(sc)) {
                            staffMenu(sc);           // Staff Menu (with password)
                        } else {
                            System.out.println("Access denied. Returning to main menu.");
                        }
                    }
                    case 0 -> {
                        System.out.println("Exiting the program.");
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }

    private static boolean verifyStaffPassword(Scanner sc) {
        System.out.print("Enter staff password: ");
        String input = sc.nextLine();
        return input.equals(STAFF_PASSWORD);
    }

    // Customer Menu
    private static void customerMenu(Scanner sc) {
        while (true) {  // ← 반복문 추가
            System.out.println("\n[ Customer Menu ]");
            System.out.println("1. Train Booking / Inquiry");
            System.out.println("2. Reservation Management");
            System.out.println("3. User Management");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> trainBookingInquiryMenu(sc);      //끝나면 다시 customerMenu로
                case 2 -> reservationManagementMenu(sc);
                case 3 -> userManagementMenu(sc);
                case 0 -> {
                    System.out.println("Returning to Main Menu.");
                    return;  // ✅ while 루프 종료 = Main Menu로 돌아감
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void trainBookingInquiryMenu(Scanner sc) {
        while (true) {
            System.out.println("\n[ Train Booking / Inquiry ]");
            System.out.println("1. Book Train Ticket");
            System.out.println("2. View Available Trains");
            //System.out.println("3. View Reservation Details");
            //System.out.println("4. Confirm Train Ticket Booking");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> InsertMenu.insertReservation(sc);
                case 2 -> SelectMenu.showRemainingSeatsMenu(sc);
                //case 3 -> SelectMenu.showUserReservations(sc);
                // case 4 -> SelectMenu.confirmReservation(sc);
                case 0 -> {
                    System.out.println("Returning to Customer Menu.");
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }


    private static void reservationManagementMenu(Scanner sc) {
        while (true) {
            System.out.println("\n[ Reservation Management ]");
            System.out.println("1. View Reservation");
            System.out.println("2. Seat Change");
            System.out.println("3. Cancel Reservation");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> SelectMenu.showUserReservations(sc);
                case 2 -> UpdateMenu.updateReservationSeat(sc);
                case 3 -> DeleteMenu.deleteReservation(sc);
                case 0 -> {
                    System.out.println("Returning to Customer Menu.");
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }


    private static void userManagementMenu(Scanner sc) {
        while (true) {
            System.out.println("\n[ User Management ]");
            System.out.println("1. Register User");
            System.out.println("2. Edit User Information");
            System.out.println("3. Delete User");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> InsertMenu.insertUser(sc);
                case "2" -> UpdateMenu.updateUserInfo(sc);
                case "3" -> DeleteMenu.deleteUser(sc);
                case "0" -> {
                    System.out.println("Returning to Customer Menu.");
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }


    private static void staffMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Staff Menu ===");
            System.out.println("1. Register Train");
            System.out.println("2. Register Route");
            System.out.println("3. Register Schedule");
            System.out.println("4. Register Seat");
            System.out.println("5. View Reservation");
            System.out.println("6. View Train Information");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> InsertMenu.insertTrain(sc);
                case 2 -> InsertMenu.insertRoute(sc);
                case 3 -> InsertMenu.insertSchedule(sc);
                case 4 -> InsertMenu.insertSeat(sc);
                case 5 -> SelectMenu.showReservations();
                // case 6 -> SelectMenu.showTrainInfo(sc);
                case 0 -> {
                    System.out.println("Returning to Main Menu.");
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

}
