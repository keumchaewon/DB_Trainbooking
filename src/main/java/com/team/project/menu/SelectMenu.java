package com.team.project.menu;

import com.team.project.ConnectionManager;
import java.sql.*;
import java.util.Scanner;

public class SelectMenu {
    public static void run(Scanner sc) {
        System.out.println("\n[조회 메뉴]");
        System.out.println("1. 사용자 목록 조회");
        System.out.println("2. 예약 내역 조회");
        System.out.println("3. 잔여 좌석 조회");
        System.out.println("4. 전체 예약 목록");
        System.out.println("5. 조건 검색 (사용자 이름)");
        System.out.print("선택: ");
        int choice = sc.nextInt();
        sc.nextLine();  // 개행 제거

        try (Connection conn = ConnectionManager.getConnection()) {
            switch (choice) {
                case 1 -> showUsers(conn);
                case 2 -> showReservations(conn);
                case 3 -> showRemainingSeats(conn);
                case 4 -> showAllReservations(conn);
                case 5 -> searchReservationByCondition(conn, sc);
                default -> System.out.println("잘못된 선택입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showUsers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM User";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[사용자 목록]");
            while (rs.next()) {
                System.out.printf("ID: %d, 이름: %s, 전화: %s, 이메일: %s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"));
            }
        }
    }

    private static void showReservations(Connection conn) throws SQLException {
        String sql = "SELECT * FROM UserReservations"; // View 사용
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[예약 내역]");
            while (rs.next()) {
                System.out.printf("이름: %s, 열차: %s, 날짜: %s, 좌석: %s%n",
                        rs.getString("user_name"),
                        rs.getString("train_name"),
                        rs.getDate("run_date"),
                        rs.getString("seat_number"));
            }
        }
    }

    private static void showRemainingSeats(Connection conn) throws SQLException {
        String sql = "SELECT * FROM RemainingSeats"; // View 사용
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[잔여 좌석]");
            while (rs.next()) {
                System.out.printf("좌석 ID: %d, 스케줄 ID: %d, 좌석 번호: %s%n",
                        rs.getInt("seat_id"),
                        rs.getInt("schedule_id"),
                        rs.getString("seat_number"));
            }
        }
    }

    private static void showAllReservations(Connection conn) throws SQLException {
        String sql = """
            SELECT u.name AS user_name, r.reservation_id, t.train_name, s.run_date, st.seat_number
            FROM Reservation r
            JOIN User u ON r.user_id = u.user_id
            JOIN Schedule s ON r.schedule_id = s.schedule_id
            JOIN Train t ON s.train_id = t.train_id
            JOIN Seat st ON r.seat_id = st.seat_id
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[전체 예약 목록]");
            while (rs.next()) {
                System.out.printf("이름: %s, 열차: %s, 날짜: %s, 좌석: %s%n",
                        rs.getString("user_name"),
                        rs.getString("train_name"),
                        rs.getDate("run_date"),
                        rs.getString("seat_number"));
            }
        }
    }

    private static void searchReservationByCondition(Connection conn, Scanner sc) throws SQLException {
        System.out.print("조회할 사용자 이름 입력: ");
        String inputName = sc.nextLine();

        String sql = """
            SELECT u.name AS user_name, t.train_name, s.run_date, st.seat_number
            FROM Reservation r
            JOIN User u ON r.user_id = u.user_id
            JOIN Schedule s ON r.schedule_id = s.schedule_id
            JOIN Train t ON s.train_id = t.train_id
            JOIN Seat st ON r.seat_id = st.seat_id
            WHERE u.name LIKE ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + inputName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("[조건 검색 결과]");
                while (rs.next()) {
                    System.out.printf("이름: %s, 열차: %s, 날짜: %s, 좌석: %s%n",
                            rs.getString("user_name"),
                            rs.getString("train_name"),
                            rs.getDate("run_date"),
                            rs.getString("seat_number"));
                }
            }
        }
    }
}
