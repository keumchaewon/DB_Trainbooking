package com.team.project.menu;
import java.util.Scanner;
import com.team.project.ConnectionManager;
import com.team.project.Main;

import java.sql.*;

public class UpdateMenu {
    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 수정 메뉴 ---");
            System.out.println("1. 사용자 정보 수정");
            System.out.println("2. 예약 정보 수정");
            System.out.println("0. 메인 메뉴로 돌아가기");
            System.out.print("선택: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> UpdateMenu.updateUserInfo(scanner);
                case "2" -> UpdateMenu.updateReservation(scanner);
                case "0" -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }
    public static void updateUserInfo(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter user ID to update: ");
            int userId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter new phone number: ");
            String newPhone = scanner.nextLine();

            System.out.print("Enter new email address: ");
            String newEmail = scanner.nextLine();

            String sql = "UPDATE User SET phone = ?, email = ? WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPhone);
            pstmt.setString(2, newEmail);
            pstmt.setInt(3, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User contact updated successfully.");
            } else {
                System.out.println("User ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateReservation(Scanner scanner) { //transaction
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                System.out.print("Enter reservation ID to update: ");
                int reservationId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter new seat ID: ");
                int newSeatId = Integer.parseInt(scanner.nextLine());

                // 1. 현재 reservation에 연결된 seat_id 가져오기
                String getOldSeatSql = "SELECT seat_id FROM Reservation WHERE reservation_id = ?";
                PreparedStatement getOldSeatStmt = conn.prepareStatement(getOldSeatSql);
                getOldSeatStmt.setInt(1, reservationId);
                ResultSet rs = getOldSeatStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Reservation ID not found.");
                    conn.rollback();
                    return;
                }

                int oldSeatId = rs.getInt("seat_id");

                // 2. Reservation의 seat_id 변경
                String updateReservationSql = "UPDATE Reservation SET seat_id = ? WHERE reservation_id = ?";
                PreparedStatement updateReservationStmt = conn.prepareStatement(updateReservationSql);
                updateReservationStmt.setInt(1, newSeatId);
                updateReservationStmt.setInt(2, reservationId);
                updateReservationStmt.executeUpdate();

                // 3. 이전 좌석의 is_reserved = false
                String unreserveSql = "UPDATE Seat SET is_reserved = FALSE WHERE seat_id = ?";
                PreparedStatement unreserveStmt = conn.prepareStatement(unreserveSql);
                unreserveStmt.setInt(1, oldSeatId);
                unreserveStmt.executeUpdate();

                // 4. 새 좌석의 is_reserved = true
                String reserveSql = "UPDATE Seat SET is_reserved = TRUE WHERE seat_id = ?";
                PreparedStatement reserveStmt = conn.prepareStatement(reserveSql);
                reserveStmt.setInt(1, newSeatId);
                reserveStmt.executeUpdate();

                // 5. 커밋
                conn.commit();
                System.out.println("Reservation seat updated and seat status synchronized.");

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
