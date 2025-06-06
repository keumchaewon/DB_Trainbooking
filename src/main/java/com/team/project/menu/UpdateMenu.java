package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class UpdateMenu {

    //1. 사용자 정보 편집
    public static void updateUserInfo(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            updateUserInfo(conn, sc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateUserInfo(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter your current name: ");
        String name = sc.nextLine();

        String findSql = "SELECT user_id FROM user WHERE name = ?";
        int userId;

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setString(1, name);
            ResultSet rs = findStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("User not found.");
                return;
            }
            userId = rs.getInt("user_id");
        }

        // 메뉴 출력
        System.out.println("\nWhich information do you want to update?");
        System.out.println("1. Name");
        System.out.println("2. Phone number");
        System.out.println("3. Email");
        System.out.print("Select option: ");
        int option = Integer.parseInt(sc.nextLine());

        String updateSql = null;
        String newValue = null;
        String columnName = null;

        switch (option) {
            case 1 -> {
                System.out.print("Enter new name: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET name = ? WHERE user_id = ?";
                columnName = "Name";
            }
            case 2 -> {
                System.out.print("Enter new phone number: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET phone = ? WHERE user_id = ?";
                columnName = "Phone number";
            }
            case 3 -> {
                System.out.print("Enter new email: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET email = ? WHERE user_id = ?";
                columnName = "Email";
            }
            default -> {
                System.out.println("Invalid selection.");
                return;
            }
        }

        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newValue);
            updateStmt.setInt(2, userId);
            int updated = updateStmt.executeUpdate();
            System.out.println(updated > 0 ? columnName + " updated successfully!" : "Update failed.");
        }
    }


    // 2. 사용자 예약 변경
    public static void updateReservationSeat(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            updateReservationSeat(conn, sc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservationSeat(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);

        try (
                PreparedStatement findUser = conn.prepareStatement("SELECT user_id FROM user WHERE name = ?");
                PreparedStatement findReservations = conn.prepareStatement(
                        "SELECT r.reservation_id, s.seat_number " +
                                "FROM reservation r JOIN seat s ON r.seat_id = s.seat_id " +
                                "WHERE r.user_id = ?"
                );
                PreparedStatement updateRes = conn.prepareStatement("UPDATE reservation SET seat_id = ? WHERE reservation_id = ?");
                PreparedStatement markSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 1 WHERE seat_id = ?");
                PreparedStatement freeOldSeat = conn.prepareStatement(
                        "UPDATE seat SET is_reserved = 0 WHERE seat_id = (SELECT seat_id FROM reservation WHERE reservation_id = ?)"
                );
                PreparedStatement getSeatId = conn.prepareStatement("SELECT seat_id FROM seat WHERE seat_number = ?")
        ) {
            System.out.print("Enter your name: ");
            String name = sc.nextLine();

            findUser.setString(1, name);
            ResultSet userRs = findUser.executeQuery();

            if (!userRs.next()) {
                System.out.println("User not found.");
                return;
            }

            int userId = userRs.getInt("user_id");

            findReservations.setInt(1, userId);
            ResultSet resRs = findReservations.executeQuery();

            System.out.println("\n[Your Reservations]");
            int count = 0;
            while (resRs.next()) {
                int resId = resRs.getInt("reservation_id");
                String seatNum = resRs.getString("seat_number");
                System.out.printf("Reservation ID: %d | Seat: %s\n", resId, seatNum);
                count++;
            }

            if (count == 0) {
                System.out.println("No reservations found.");
                return;
            }

            System.out.print("Enter Reservation ID to update: ");
            int resId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter new Seat Number (e.g., A1): ");
            String newSeatNumber = sc.nextLine();

            getSeatId.setString(1, newSeatNumber);
            ResultSet seatRs = getSeatId.executeQuery();

            if (!seatRs.next()) {
                System.out.println("Seat not found.");
                conn.rollback();
                return;
            }

            int newSeatId = seatRs.getInt("seat_id");

            freeOldSeat.setInt(1, resId);
            freeOldSeat.executeUpdate();

            updateRes.setInt(1, newSeatId);
            updateRes.setInt(2, resId);
            markSeat.setInt(1, newSeatId);

            updateRes.executeUpdate();
            markSeat.executeUpdate();

            conn.commit();
            System.out.println("Seat change completed.");

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Error occurred during seat update.");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }

}
