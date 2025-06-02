package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class InsertMenu {

    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 등록 메뉴 ---");
            System.out.println("1. 사용자 등록");
            System.out.println("2. 열차 등록");
            System.out.println("3. 노선 등록");
            System.out.println("4. 스케줄 등록");
            System.out.println("5. 좌석 등록");
            System.out.println("6. 예약 등록");
            System.out.println("0. 메인 메뉴로 돌아가기");
            System.out.print("선택: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> insertUser(scanner);
                case "2" -> insertTrain(scanner);
                case "3" -> insertRoute(scanner);
                case "4" -> insertSchedule(scanner);
                case "5" -> insertSeat(scanner);
                case "6" -> insertReservation(scanner);
                case "0" -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public static void insertUser(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter name:");
            String name = scanner.nextLine();

            System.out.print("Enter phone number:");
            String phone = scanner.nextLine();

            System.out.print("Enter email address:");
            String email = scanner.nextLine();

            String sql = "INSERT INTO User(name, phone, email) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("User inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertTrain(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter train_name:");
            String train_name = scanner.nextLine();

            System.out.print("Enter train_type:");
            String train_type = scanner.nextLine();

            String sql = "INSERT INTO Train(train_name, train_type) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, train_name);
            pstmt.setString(2, train_type);
            pstmt.executeUpdate();
            System.out.println("Train inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRoute(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter start_station:");
            String start_station = scanner.nextLine();

            System.out.print("Enter end_station:");
            String end_station = scanner.nextLine();

            String sql = "INSERT INTO Route(start_station, end_station) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, start_station);
            pstmt.setString(2, end_station);
            pstmt.executeUpdate();
            System.out.println("Route inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSchedule(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.print("Enter train_id:");
            int train_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter route_id:");
            int route_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter run_date (yyyy-mm-dd):");
            Date run_date = Date.valueOf(scanner.nextLine());

            System.out.print("Enter departure_time (HH:mm:ss):");
            Time departure_time = Time.valueOf(scanner.nextLine());

            String sql = "INSERT INTO Schedule(train_id, route_id, run_date, departure_time) VALUES (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, train_id);
            pstmt.setInt(2, route_id);
            pstmt.setDate(3, run_date);
            pstmt.setTime(4, departure_time);
            pstmt.executeUpdate();
            System.out.println("Schedule inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            System.out.println("Seat inserted!");
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
