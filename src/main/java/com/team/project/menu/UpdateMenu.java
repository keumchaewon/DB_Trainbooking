package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class UpdateMenu {
    public static void run(Scanner sc) {
        while(true) {
            System.out.println("\n[Update Menu]");
            System.out.println("1. Edit user contact information");
            System.out.println("2. Change reserved seat");
            System.out.print("Select option: ");

            String input = sc.nextLine();
            if (input.equals("0")) return;

            switch (input) {
                case "1" -> {
                    try (Connection conn = ConnectionManager.getConnection()) {
                        updateUserContact(conn, sc);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                case "2" -> {
                    try (Connection conn = ConnectionManager.getConnection()) {
                        updateReservationSeat(conn, sc);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                default -> System.out.println("Invalid Option.");
            }
        }
    }

    private static void updateUserContact(Connection conn, Scanner sc) throws SQLException {
        System.out.print("User ID to modify: ");
        int userId = Integer.parseInt(sc.nextLine());
        System.out.print("New phone number: ");
        String phone = sc.nextLine();
        System.out.print("New email: ");
        String email = sc.nextLine();

        String sql = "UPDATE user SET phone = ?, email = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setInt(3, userId);
            int updated = pstmt.executeUpdate();
            System.out.println(updated > 0 ? "Modified successfully!" : "No matched user");
        }
    }

    private static void updateReservationSeat(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        try (
            PreparedStatement updateRes = conn.prepareStatement("UPDATE reservation SET seat_id = ? WHERE reservation_id = ?");
            PreparedStatement markSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 1 WHERE seat_id = ?");
        ) {
            System.out.print("Reservation ID: ");
            int resId = Integer.parseInt(sc.nextLine());
            System.out.print("New seat ID: ");
            int seatId = Integer.parseInt(sc.nextLine());

            updateRes.setInt(1, seatId);
            updateRes.setInt(2, resId);
            markSeat.setInt(1, seatId);

            updateRes.executeUpdate();
            markSeat.executeUpdate();

            conn.commit();
            System.out.println("Seat change completed");
        } catch (Exception e) {
            conn.rollback();
            System.out.println("Error!");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}

