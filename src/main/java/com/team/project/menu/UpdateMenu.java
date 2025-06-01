package com.team.project.menu;

import java.util.Scanner;

import com.team.project.ConnectionManager;

import java.sql.*;
import java.util.Scanner;

public class UpdateMenu {
    public static void run(Scanner sc) {
        System.out.println("\n[Update Menu]");
        System.out.println("1. 사용자 연락처 수정");
        System.out.println("2. 예약 좌석 변경");
        System.out.print("선택: ");
        int choice = sc.nextInt();

        try (Connection conn = ConnectionManager.getConnection()) {
            switch (choice) {
                case 1 -> updateUserContact(conn, sc);
                case 2 -> updateReservationSeat(conn, sc);
                default -> System.out.println("잘못된 선택");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 연락처 수정
    private static void updateUserContact(Connection conn, Scanner sc) throws SQLException {
        System.out.print("수정할 사용자 ID: ");
        int userId = sc.nextInt(); sc.nextLine();
        System.out.print("새 전화번호: ");
        String phone = sc.nextLine();
        System.out.print("새 이메일: ");
        String email = sc.nextLine();

        String sql = "UPDATE user SET phone = ?, email = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setInt(3, userId);
            int updated = pstmt.executeUpdate();
            System.out.println(updated > 0 ? "수정 완료" : " 사용자 없음");
        }
    }

    // 예약 좌석 변경
    private static void updateReservationSeat(Connection conn, Scanner sc) throws SQLException {
        // 트랜젝션
        conn.setAutoCommit(false);
        try (
                PreparedStatement updateRes = conn.prepareStatement("UPDATE reservation SET seat_id = ? WHERE reservation_id = ?"); //예약한 좌석
                PreparedStatement markSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 1 WHERE seat_id = ?"); //바꿀 좌석
        ) {
            System.out.print("예약 ID: ");
            int resId = sc.nextInt();
            System.out.print("새 좌석 ID: ");
            int seatId = sc.nextInt();

            updateRes.setInt(1, seatId);
            updateRes.setInt(2, resId);
            markSeat.setInt(1, seatId);

            updateRes.executeUpdate();
            markSeat.executeUpdate();

            conn.commit();
            System.out.println("좌석 변경 완료");
        } catch (Exception e) {
            conn.rollback();
            System.out.println("트랜잭션 롤백됨");
        } finally {
            conn.setAutoCommit(true);
        }
    }
}

