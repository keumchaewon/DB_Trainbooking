package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class InsertMenu {
    public static void run(Scanner sc) {
        System.out.println("\n[Insert Menu]");
        System.out.println("1. 사용자 등록");
        System.out.println("2. 열차 등록");
        System.out.println("3. 노선 등록");
        System.out.println("4. 스케줄 등록");
        System.out.println("5. 좌석 등록");
        System.out.println("6. 예약 등록");
        System.out.print("선택: ");
        int choice = sc.nextInt(); sc.nextLine();

        try (Connection conn = ConnectionManager.getConnection()) {
            switch (choice) {
                case 1 -> insertUser(conn, sc);
                case 2 -> insertTrain(conn, sc);
                case 3 -> insertRoute(conn, sc);
                case 4 -> insertSchedule(conn, sc);
                case 5 -> insertSeat(conn, sc);
                case 6 -> insertReservation(conn, sc);
                default -> System.out.println("잘못된 선택입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertUser(Connection conn, Scanner sc) throws SQLException {
        System.out.print("사용자 ID: ");
        int userId = Integer.parseInt(sc.nextLine());
        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("전화번호: ");
        String phone = sc.nextLine();
        System.out.print("이메일: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO user (user_id, name, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            System.out.println("사용자 등록 완료");
        }
    }

    private static void insertTrain(Connection conn, Scanner sc) throws SQLException {
        System.out.print("열차 ID: ");
        int trainId = Integer.parseInt(sc.nextLine());
        System.out.print("열차 이름: ");
        String trainName = sc.nextLine();
        System.out.print("열차 종류: ");
        String trainType = sc.nextLine();

        String sql = "INSERT INTO train (train_id, train_name, train_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);
            pstmt.setString(2, trainName);
            pstmt.setString(3, trainType);
            pstmt.executeUpdate();
            System.out.println("열차 등록 완료");
        }
    }

    private static void insertRoute(Connection conn, Scanner sc) throws SQLException {
        System.out.print("노선 ID: ");
        int routeId = Integer.parseInt(sc.nextLine());
        System.out.print("출발역: ");
        String start = sc.nextLine();
        System.out.print("도착역: ");
        String end = sc.nextLine();

        String sql = "INSERT INTO route (route_id, start_station, end_station) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, routeId);
            pstmt.setString(2, start);
            pstmt.setString(3, end);
            pstmt.executeUpdate();
            System.out.println("노선 등록 완료");
        }
    }

    private static void insertSchedule(Connection conn, Scanner sc) throws SQLException {
        System.out.print("스케줄 ID: ");
        int scheduleId = Integer.parseInt(sc.nextLine());
        System.out.print("열차 ID: ");
        int trainId = Integer.parseInt(sc.nextLine());
        System.out.print("노선 ID: ");
        int routeId = Integer.parseInt(sc.nextLine());
        System.out.print("운행 날짜 (yyyy-mm-dd): ");
        Date runDate = Date.valueOf(sc.nextLine());
        System.out.print("출발 시간 (HH:mm:ss): ");
        Time depTime = Time.valueOf(sc.nextLine());

        String sql = "INSERT INTO schedule (schedule_id, train_id, route_id, run_date, departure_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            pstmt.setInt(2, trainId);
            pstmt.setInt(3, routeId);
            pstmt.setDate(4, runDate);
            pstmt.setTime(5, depTime);
            pstmt.executeUpdate();
            System.out.println("스케줄 등록 완료");
        }
    }

    private static void insertSeat(Connection conn, Scanner sc) throws SQLException {
        System.out.print("좌석 ID: ");
        int seatId = Integer.parseInt(sc.nextLine());
        System.out.print("스케줄 ID: ");
        int scheduleId = Integer.parseInt(sc.nextLine());
        System.out.print("좌석 번호: ");
        String seatNumber = sc.nextLine();
        System.out.print("예약 여부 (true/false): ");
        boolean reserved = Boolean.parseBoolean(sc.nextLine());

        String sql = "INSERT INTO seat (seat_id, schedule_id, seat_number, is_reserved) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seatId);
            pstmt.setInt(2, scheduleId);
            pstmt.setString(3, seatNumber);
            pstmt.setBoolean(4, reserved);
            pstmt.executeUpdate();
            System.out.println("좌석 등록 완료");
        }
    }

    private static void insertReservation(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        try (
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO reservation (reservation_id, user_id, schedule_id, seat_id) VALUES (?, ?, ?, ?)");
                PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE seat SET is_reserved = TRUE WHERE seat_id = ?")
        ) {
            System.out.print("예약 ID: ");
            int resId = Integer.parseInt(sc.nextLine());
            System.out.print("사용자 ID: ");
            int userId = Integer.parseInt(sc.nextLine());
            System.out.print("스케줄 ID: ");
            int schedId = Integer.parseInt(sc.nextLine());
            System.out.print("좌석 ID: ");
            int seatId = Integer.parseInt(sc.nextLine());

            insertStmt.setInt(1, resId);
            insertStmt.setInt(2, userId);
            insertStmt.setInt(3, schedId);
            insertStmt.setInt(4, seatId);
            insertStmt.executeUpdate();

            updateStmt.setInt(1, seatId);
            updateStmt.executeUpdate();

            conn.commit();
            System.out.println("예약 등록 및 좌석 예약 완료");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("트랜잭션 롤백됨");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
