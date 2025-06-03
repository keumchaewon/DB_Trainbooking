package com.team.project.menu;

import java.util.Scanner;

import com.team.project.ConnectionManager;

import java.sql.*;

public class DeleteMenu {
    public static void run(Scanner sc) {
        System.out.println("\n[Delete Menu]");
        System.out.println("1. Cancel Reservation"); //예약 취소 + 좌석 복원
        System.out.println("2. Delete User");
        System.out.print("Select option: ");
        int choice = sc.nextInt();

        try (Connection conn = ConnectionManager.getConnection()) {
            switch (choice) {
                case 1 -> deleteReservation(conn, sc); // 1. 예약 취소
                case 2 -> deleteUser(conn, sc); // 2. 사용자 삭제
                default -> System.out.println("Invalid option.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. 예약 취소의 경우
    private static void deleteReservation(Connection conn, Scanner sc) throws SQLException {
        // 트랙잭션
        conn.setAutoCommit(false);

        try (
                PreparedStatement getSeat = conn.prepareStatement("SELECT seat_id FROM reservation WHERE reservation_id = ?");
                PreparedStatement delRes = conn.prepareStatement("DELETE FROM reservation WHERE reservation_id = ?");
                PreparedStatement freeSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 0 WHERE seat_id = ?");
        ) {
            System.out.print("Reservation ID for deletion: ");
            int resId = sc.nextInt();

            getSeat.setInt(1, resId); //좌석 찾기
            ResultSet rs = getSeat.executeQuery();

            if (rs.next()) {
                int seatId = rs.getInt("seat_id");
                delRes.setInt(1, resId); //해당 좌석 reservation 삭제
                freeSeat.setInt(1, seatId); //해당 좌석 free

                delRes.executeUpdate(); //해당 사항 update
                freeSeat.executeUpdate();

                conn.commit();
                System.out.println("Reservation deleted successfully");
            } else {
                conn.rollback();
                System.out.println("Reservation not found");
            }
        } catch (Exception e) {
            conn.rollback();
            System.out.println("Transaction failed");
        } finally {
            conn.setAutoCommit(true);
        }
    }

    //2. 사용자 삭제의 경우
    private static void deleteUser(Connection conn, Scanner sc) throws SQLException {
        System.out.print("삭제할 사용자 ID: ");
        int userId = sc.nextInt();

        String sql = "DELETE FROM user WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int deleted = pstmt.executeUpdate();
            System.out.println(deleted > 0 ? "사용자 삭제 완료" : "사용자 없음");
        }
    }
}

