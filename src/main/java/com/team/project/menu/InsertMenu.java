package com.team.project.menu;
import java.util.*;
import java.sql.*;
import java.util.Scanner;
import com.team.project.ConnectionManager;
import java.util.*;

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
            java.sql.Date run_date = java.sql.Date.valueOf(scanner.nextLine());

            System.out.print("Enter departure_time (HH:mm:ss):");
            java.sql.Time departure_time = java.sql.Time.valueOf(scanner.nextLine());

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
                String scheduleListSql = "SELECT schedule_id, train_id, route_id, run_date, departure_time FROM Schedule ORDER BY schedule_id";
                try (PreparedStatement stmt = conn.prepareStatement(scheduleListSql)) {
                    ResultSet rs = stmt.executeQuery();

                    System.out.println("Train Schedule Overview:");
                    System.out.printf("%-5s | %-7s | %-7s | %-12s | %-10s%n",
                            "ID", "TrainID", "RouteID", "Run Date", "Departure");
                    System.out.println("------------------------------------------------------");
                    while (rs.next()) {
                        int id = rs.getInt("schedule_id");
                        int trainId = rs.getInt("train_id");
                        int routeId = rs.getInt("route_id");
                        java.sql.Date runDate = rs.getDate("run_date");
                        Time departure = rs.getTime("departure_time");

                        System.out.printf("%-5d | %-7d | %-7d | %-12s | %-10s%n",
                                id, trainId, routeId, runDate.toString(), departure.toString());
                    }
                    System.out.println(); // 빈 줄 추가
                }

                // 1. schedule_id 입력
                System.out.print("Enter schedule ID: ");
                int scheduleId = Integer.parseInt(scanner.nextLine());

                // ✅ schedule_id 유효성 확인
                String checkScheduleSql = "SELECT COUNT(*) FROM Schedule WHERE schedule_id = ?";
                try (PreparedStatement checkScheduleStmt = conn.prepareStatement(checkScheduleSql)) {
                    checkScheduleStmt.setInt(1, scheduleId);
                    ResultSet rs = checkScheduleStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("No schedule found with the given ID");
                        return;
                    }
                }

                // 2. 남은 좌석 보여주기 (그림 형식 + 줄바꿈)
                System.out.println("\n🪑 전체 좌석 현황 (예약된 좌석은 -- 로 표시):");
                String showAllSeatsSql = "SELECT seat_number, is_reserved FROM Seat WHERE schedule_id = ?";

                Map<String, List<String>> seatMap = new TreeMap<>();  // A, B, C... 줄별
                try (PreparedStatement stmt = conn.prepareStatement(showAllSeatsSql)) {
                    stmt.setInt(1, scheduleId);
                    ResultSet rs = stmt.executeQuery();

                    boolean hasSeat = false;
                    Map<String, Boolean> seatReservedMap = new HashMap<>(); // seat_number → 예약 여부

                    while (rs.next()) {
                        hasSeat = true;
                        String seatNumber = rs.getString("seat_number");
                        boolean reserved = rs.getBoolean("is_reserved");
                        String row = seatNumber.replaceAll("[^A-Z]", "");  // 예: A, B 등

                        seatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatNumber);
                        seatReservedMap.put(seatNumber, reserved);
                    }

                    if (!hasSeat) {
                        System.out.println("No seat information available.");
                        return;
                    }

                    for (String row : seatMap.keySet()) {
                        System.out.print(row + "row : ");
                        List<String> seats = seatMap.get(row);

                        // 숫자 기준 정렬
                        seats.sort(Comparator.comparingInt(s -> Integer.parseInt(s.replaceAll("[^0-9]", ""))));

                        for (String sn : seats) {
                            if (seatReservedMap.getOrDefault(sn, false)) {
                                System.out.print(" --  ");
                            } else {
                                System.out.print("[" + sn + "] ");
                            }
                        }
                        System.out.println();
                    }
                    System.out.println();
                }



                // 3. seat_number 입력
                System.out.print("Enter seat number to reserve (e.g., A1): ");
                String seatNumber = scanner.nextLine().trim().toUpperCase();

                // ✅ seat_number 유효성 확인 및 seat_id 조회
                String checkSeatSql = "SELECT is_reserved, seat_id FROM Seat WHERE seat_number = ? AND schedule_id = ?";
                int seatId = -1;
                try (PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatSql)) {
                    checkSeatStmt.setString(1, seatNumber);
                    checkSeatStmt.setInt(2, scheduleId);
                    ResultSet rs = checkSeatStmt.executeQuery();

                    if (!rs.next()) {
                        System.out.println("The selected seat does not exist or is not part of the schedule.");
                        return;
                    }
                    if (rs.getBoolean("is_reserved")) {
                        System.out.println("This seat has already been reserved.");
                        return;
                    }
                    seatId = rs.getInt("seat_id");
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
                        System.out.println("No user found with the given ID.");
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
                System.out.println("Reservation completed successfully!");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println(" An error occurred while processing the reservation.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }


}
