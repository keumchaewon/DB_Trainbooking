package com.team.project.menu;

import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;

public class InsertMenu {

    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n[Insert Menu]");
            System.out.println("1. User Registration");
            System.out.println("2. Train Registration");
            System.out.println("3. Route Registration");
            System.out.println("4. Schedule Registration");
            System.out.println("5. Seat Registration");
            System.out.println("6. Reservation Registration");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> insertUser(scanner);
                case "2" -> insertTrain(scanner);
                case "3" -> insertRoute(scanner);
                case "4" -> insertSchedule(scanner);
                case "5" -> insertSeat(scanner);
                case "6" -> insertReservation(scanner);
                default -> System.out.println("Invalid option.");
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
            System.out.println("User registered!");
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
            System.out.println("Train registered!");
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
            System.out.println("Route registered!");
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
            System.out.println("Schedule registered!");
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
            System.out.println("Seat registered!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertReservation(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. schedule_id 입력
                System.out.print("Enter schedule ID: ");
                int scheduleId = Integer.parseInt(scanner.nextLine());

                // ✅ schedule_id 유효성 확인
                String checkScheduleSql = "SELECT COUNT(*) FROM Schedule WHERE schedule_id = ?";
                try (PreparedStatement checkScheduleStmt = conn.prepareStatement(checkScheduleSql)) {
                    checkScheduleStmt.setInt(1, scheduleId);
                    ResultSet rs = checkScheduleStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("❗해당 schedule_id는 존재하지 않습니다.");
                        return;
                    }
                }

                // 2. 남은 좌석 보여주기
                System.out.println("📋 예약 가능한 좌석 목록:");
                String showSeatsSql = "SELECT seat_id FROM Seat WHERE schedule_id = ? AND is_reserved = FALSE";
                try (PreparedStatement showStmt = conn.prepareStatement(showSeatsSql)) {
                    showStmt.setInt(1, scheduleId);
                    ResultSet rs = showStmt.executeQuery();

                    boolean hasAvailable = false;
                    while (rs.next()) {
                        hasAvailable = true;
                        System.out.println(" - Seat ID: " + rs.getInt("seat_id"));
                    }

                    if (!hasAvailable) {
                        System.out.println("❗예약 가능한 좌석이 없습니다.");
                        return;
                    }
                }

                // 3. seat_id 선택
                System.out.print("Enter seat ID to reserve: ");
                int seatId = Integer.parseInt(scanner.nextLine());

                // ✅ seat_id 유효성 확인
                String checkSeatSql = "SELECT is_reserved FROM Seat WHERE seat_id = ? AND schedule_id = ?";
                try (PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatSql)) {
                    checkSeatStmt.setInt(1, seatId);
                    checkSeatStmt.setInt(2, scheduleId);
                    ResultSet rs = checkSeatStmt.executeQuery();

                    if (!rs.next()) {
                        System.out.println("❗해당 좌석은 존재하지 않거나 스케줄에 포함되지 않습니다.");
                        return;
                    }
                    if (rs.getBoolean("is_reserved")) {
                        System.out.println("❗이미 예약된 좌석입니다.");
                        return;
                    }
                }

                // 4. user_id 입력
                System.out.print("Enter user ID: ");
                int userId = Integer.parseInt(scanner.nextLine());

                // ✅ user_id 유효성 확인
                String checkUserSql = "SELECT COUNT(*) FROM User WHERE user_id = ?";
                try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                    checkUserStmt.setInt(1, userId);
                    ResultSet rs = checkUserStmt.executeQuery();

                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("❗해당 user_id는 존재하지 않습니다.");
                        return;
                    }
                }

                // 5. 예약 등록
                String insertSql = "INSERT INTO Reservation (user_id, schedule_id, seat_id) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, scheduleId);
                    insertStmt.setInt(3, seatId);
                    insertStmt.executeUpdate();
                }

                // 6. 좌석 상태 업데이트
                String updateSql = "UPDATE Seat SET is_reserved = TRUE WHERE seat_id = ? AND schedule_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, seatId);
                    updateStmt.setInt(2, scheduleId);
                    updateStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("✅ 예약이 성공적으로 완료되었습니다!");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("🚫 예약 처리 중 오류가 발생했습니다.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("🚫 DB 연결에 실패했습니다.");
            e.printStackTrace();
        }
    }

}
