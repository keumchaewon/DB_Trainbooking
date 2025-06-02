package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class InsertMenu {
    public static void insertSeat(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter schedule_id:");
            int schedule_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter seat_number:");
            String seat_number = scanner.nextLine();

            System.out.print("Enter is_reserved:");
            Boolean is_reserved = Boolean.parseBoolean(scanner.nextLine());

            String sql = "INSERT INTO Seat(schedule_id, seat_number, is_reserved) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, schedule_id);
            pstmt.setString(2, seat_number);
            pstmt.setBoolean(3, is_reserved);
            pstmt.executeUpdate();
            System.out.println("Route inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertReservation(Scanner scanner) { //transaction
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                System.out.print("Enter user ID: ");
                int userId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter schedule ID: ");
                int scheduleId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter seat ID: ");
                int seatId = Integer.parseInt(scanner.nextLine());

                // 1. 예약 삽입
                String insertSql = "INSERT INTO Reservation (user_id, schedule_id, seat_id) VALUES ( ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, scheduleId);
                insertStmt.setInt(3, seatId);
                insertStmt.executeUpdate();

                // 2. 좌석 예약 상태 변경
                String updateSql = "UPDATE Seat SET is_reserved = TRUE WHERE seat_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, seatId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("Reservation inserted and seat marked as reserved.");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction rolled back due to error.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

