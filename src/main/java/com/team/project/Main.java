package com.team.project;

import com.team.project.menu.DeleteMenu;
import com.team.project.menu.InsertMenu;
import com.team.project.menu.SelectMenu;
import com.team.project.menu.UpdateMenu;
import java.util.Scanner;

public class Main {
    private static final String STAFF_PASSWORD = "nollback";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while(true) {
                System.out.println("\n=== Train Reservation System ===");
                System.out.println("1. Customer Menu");
                System.out.println("2. Staff Menu");
                System.out.println("0. Exit");
                System.out.print("Please select a menu: ");

                String input = sc.nextLine();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 0:
                        System.out.println("Exiting the program.");
                        return;
                    case 1:
                        customerMenu(sc);
                        break;
                    case 2:
                        if (verifyStaffPassword(sc)) {
                            staffMenu(sc);
                        } else {
                            System.out.println("Access denied. Returning to main menu.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }

    private static boolean verifyStaffPassword(Scanner sc) {
        System.out.print("Enter staff password: ");
        String input = sc.nextLine();
        return input.equals(STAFF_PASSWORD);
    }

    private static void customerMenu(Scanner sc) {
        while(true) {
            System.out.println("\n[ Customer Menu ]");
            System.out.println("1. Train Booking / Inquiry");
            System.out.println("2. Reservation Management");
            System.out.println("3. User Management");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Returning to Main Menu.");
                    return;
                case 1:
                    trainBookingInquiryMenu(sc);
                    break;
                case 2:
                    reservationManagementMenu(sc);
                    break;
                case 3:
                    userManagementMenu(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void trainBookingInquiryMenu(Scanner sc) {
        while(true) {
            System.out.println("\n[ Train Booking / Inquiry ]");
            System.out.println("1. Book Train Ticket");
            System.out.println("2. View Available Trains");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Returning to Customer Menu.");
                    return;
                case 1:
                    InsertMenu.insertReservation(sc);
                    break;
                case 2:
                    SelectMenu.showRemainingSeatsMenu(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void reservationManagementMenu(Scanner sc) {
        while(true) {
            System.out.println("\n[ Reservation Management ]");
            System.out.println("1. View Reservation");
            System.out.println("2. Seat Change");
            System.out.println("3. Cancel Reservation");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Returning to Customer Menu.");
                    return;
                case 1:
                    SelectMenu.showUserReservations(sc);
                    break;
                case 2:
                    UpdateMenu.updateReservationSeat(sc);
                    break;
                case 3:
                    DeleteMenu.deleteReservation(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void userManagementMenu(Scanner sc) {
        while(true) {
            System.out.println("\n[ User Management ]");
            System.out.println("1. Register User");
            System.out.println("2. Edit User Information");
            System.out.println("3. Delete User");
            System.out.println("0. Back to Customer Menu");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            switch (input) {
                case "1":
                    InsertMenu.insertUser(sc);
                    break;
                case "2":
                    UpdateMenu.updateUserInfo(sc);
                    break;
                case "3":
                    DeleteMenu.deleteUser(sc);
                    break;
                case "0":
                    System.out.println("Returning to Customer Menu.");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void staffMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Staff Menu ===");
            System.out.println("1. Register Menu");
            System.out.println("2. View Menu");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Returning to Main Menu.");
                    return;
                case 1:
                    registerMenu(sc);
                    break;
                case 2:
                    viewMenu(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void registerMenu(Scanner sc) {
        while (true) {
            System.out.println("\n[ Register Menu ]");
            System.out.println("1. Register Train");
            System.out.println("2. Register Route");
            System.out.println("3. Register Schedule");
            System.out.println("4. Register Seat");
            System.out.println("0. Back");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    return;
                case 1:
                    InsertMenu.insertTrain(sc);
                    break;
                case 2:
                    InsertMenu.insertRoute(sc);
                    break;
                case 3:
                    InsertMenu.insertSchedule(sc);
                    break;
                case 4:
                    InsertMenu.insertSeat(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void viewMenu(Scanner sc) {
        while (true) {
            System.out.println("\n[ View Menu ]");
            System.out.println("1. View Reservation Summary by Date");
            System.out.println("2. View Reserved Seat Details");
            System.out.println("3. View All Users");
            System.out.println("0. Back");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 0:
                    return;
                case 1:
                    SelectMenu.showReservations();
                    break;
                case 2:
                    SelectMenu.viewReservedSeatInfo(sc);
                    break;
                case 3:
                    SelectMenu.showUsers();
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

}

