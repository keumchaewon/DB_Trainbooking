package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class UpdateMenu {
    public static void run(Scanner sc) {
        while(true) {
            System.out.println("\n--- 수정 메뉴 ---");
            System.out.println("1. 사용자 연락처 수정");
            System.out.println("2. 예약 좌석 변경");
            System.out.println("0. 메인 메뉴로 돌아가기");
            System.out.print("선택: ");

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
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private static void updateUserContact(Connection conn, Scanner sc) throws SQLException {
        System.out.print("수정할 사용자 ID: ");
        int userId = Integer.parseInt(sc.nextLine());
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
            System.out.println(updated > 0 ? "수정 완료" : "사용자 없음");
        }
    }

    private static void updateReservationSeat(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        try (
            PreparedStatement updateRes = conn.prepareStatement("UPDATE reservation SET seat_id = ? WHERE reservation_id = ?");
            PreparedStatement markSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 1 WHERE seat_id = ?");
        ) {
            System.out.print("예약 ID: ");
            int resId = Integer.parseInt(sc.nextLine());
            System.out.print("새 좌석 ID: ");
            int seatId = Integer.parseInt(sc.nextLine());

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
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
