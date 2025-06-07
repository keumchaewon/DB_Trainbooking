package com.team.project.menu;

import com.team.project.ConnectionManager;
import java.sql.*;
import java.util.Scanner;
import java.util.*;

public class SelectMenu {

    private static void showUsers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM User";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[User List]");
            while (rs.next()) {
                System.out.printf("ID: %d, name: %s, phone number: %s, email: %s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"));
            }
        }
    }

    public static void showReservations() {
        try (Connection conn = ConnectionManager.getConnection()) {
            showReservations(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showReservations(Connection conn) throws SQLException {
        String sql = "SELECT * FROM UserReservations"; // View 사용
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[Reservation List]");
            while (rs.next()) {
                System.out.printf("name: %s, train: %s, date: %s, seat: %s%n",
                        rs.getString("user_name"),
                        rs.getString("train_name"),
                        rs.getDate("run_date"),
                        rs.getString("seat_number"));
            }
        }
    }


    public static void showRemainingSeats() {
        try (Connection conn = ConnectionManager.getConnection()) {
            showRemainingSeats(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showRemainingSeats(Connection conn) throws SQLException {
        // ✅ 0. 스케줄 목록 + 남은 좌석 수 출력
        String scheduleListSql =
                "SELECT s.schedule_id, s.train_id, s.route_id, s.run_date, s.departure_time, " +
                        "       COUNT(seat_id) AS remaining_seats " +
                        "FROM Schedule s " +
                        "LEFT JOIN Seat seat ON s.schedule_id = seat.schedule_id AND seat.is_reserved = FALSE " +
                        "GROUP BY s.schedule_id, s.train_id, s.route_id, s.run_date, s.departure_time " +
                        "ORDER BY s.schedule_id";

        try (PreparedStatement stmt = conn.prepareStatement(scheduleListSql)) {
            ResultSet rs = stmt.executeQuery();

            System.out.println("📅 현재 등록된 스케줄 목록:");
            System.out.printf("%-5s | %-7s | %-7s | %-12s | %-10s | %-5s%n",
                    "ID", "TrainID", "RouteID", "Run Date", "Departure", "잔여좌석");
            System.out.println("------------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("schedule_id");
                int trainId = rs.getInt("train_id");
                int routeId = rs.getInt("route_id");
                java.sql.Date runDate = rs.getDate("run_date");
                Time departure = rs.getTime("departure_time");
                int remaining = rs.getInt("remaining_seats");

                System.out.printf("%-5d | %-7d | %-7d | %-12s | %-10s | %-5d%n",
                        id, trainId, routeId, runDate.toString(), departure.toString(), remaining);
            }
            System.out.println();
        }



    }


    public static void showAllReservations() {
        try (Connection conn = ConnectionManager.getConnection()) {
            showAllReservations(conn);
        } catch (SQLException e) {
            e.printStackTrace();
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
            System.out.println("[Reservation list]");
            while (rs.next()) {
                System.out.printf("name: %s, train: %s, date: %s, seat: %s%n",
                        rs.getString("user_name"),
                        rs.getString("train_name"),
                        rs.getDate("run_date"),
                        rs.getString("seat_number"));
            }
        }
    }

    public static void showUserReservations(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        String sql = """
        SELECT u.name AS user_name, t.train_name, s.run_date, st.seat_number
        FROM Reservation r
        JOIN User u ON r.user_id = u.user_id
        JOIN Schedule s ON r.schedule_id = s.schedule_id
        JOIN Train t ON s.train_id = t.train_id
        JOIN Seat st ON r.seat_id = st.seat_id
        WHERE u.name = ? AND u.email = ?
    """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n[Your Reservations]");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf(" - Train: %s | Date: %s | Seat: %s%n",
                        rs.getString("train_name"),
                        rs.getDate("run_date").toString(),
                        rs.getString("seat_number"));
            }

            if (!found) {
                System.out.println("No reservations found for this user.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to load reservations.");
            e.printStackTrace();
        }
    }




    private static void searchReservationByCondition(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter the user name to view: ");
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
                System.out.println("[Search results by condition]");
                while (rs.next()) {
                    System.out.printf("name: %s, train: %s, date: %s, seat: %s%n",
                            rs.getString("user_name"),
                            rs.getString("train_name"),
                            rs.getDate("run_date"),
                            rs.getString("seat_number"));
                }
            }
        }
    }

    public static void showRemainingSeatsMenu(Scanner scanner) {
        while (true) {
            // 1. Show all available schedules with remaining seats
            String scheduleListSql =
                    "SELECT s.schedule_id, s.train_id, s.route_id, s.run_date, s.departure_time, " +
                            "       COUNT(seat_id) AS remaining_seats " +
                            "FROM Schedule s " +
                            "LEFT JOIN Seat seat ON s.schedule_id = seat.schedule_id AND seat.is_reserved = FALSE " +
                            "GROUP BY s.schedule_id, s.train_id, s.route_id, s.run_date, s.departure_time " +
                            "ORDER BY s.schedule_id";

            try (
                    Connection conn = ConnectionManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(scheduleListSql);
                    ResultSet rs = stmt.executeQuery()
            ) {
                // ✅ 여기에 모든 출력 로직 넣기
                System.out.println(" Current Schedule List:");
                System.out.printf("%-5s | %-8s | %-8s | %-12s | %-10s | %-15s%n",
                        "ID", "Train ID", "Route ID", "Run Date", "Departure", "Available Seats");
                System.out.println("-------------------------------------------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("schedule_id");
                    int trainId = rs.getInt("train_id");
                    int routeId = rs.getInt("route_id");
                    java.sql.Date runDate = rs.getDate("run_date");
                    Time departure = rs.getTime("departure_time");
                    int remaining = rs.getInt("remaining_seats");

                    System.out.printf("%-5d | %-8d | %-8d | %-12s | %-10s | %-15d%n",
                            id, trainId, routeId, runDate.toString(), departure.toString(), remaining);
                }
                System.out.println();

            } catch (SQLException e) {
                System.out.println("Failed to load schedule list.");
                e.printStackTrace();
            }




            // 2. Ask user to input a schedule ID to see seat details
            System.out.print("\nEnter the Schedule ID you want to check: ");
            int inputId = Integer.parseInt(scanner.nextLine());

            // 3. Show seat details (formatted by row/column)
            String detailSql = "SELECT seat_number, is_reserved FROM Seat WHERE schedule_id = ?";

            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {

                detailStmt.setInt(1, inputId);
                ResultSet rs = detailStmt.executeQuery();

                Map<String, List<String>> seatMap = new TreeMap<>();               // row 별 좌석 저장
                Map<String, Boolean> seatReservedMap = new HashMap<>();            // seat_number → 예약 여부
                boolean hasSeat = false;

                while (rs.next()) {
                    hasSeat = true;
                    String seatNumber = rs.getString("seat_number");
                    boolean reserved = rs.getBoolean("is_reserved");
                    String row = seatNumber.replaceAll("[^A-Z]", "");

                    seatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatNumber);
                    seatReservedMap.put(seatNumber, reserved);
                }

                // 잔여 좌석 수 출력
                String countSql = "SELECT COUNT(*) AS remaining FROM Seat WHERE schedule_id = ? AND is_reserved = FALSE";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    countStmt.setInt(1, inputId);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        int remaining = countRs.getInt("remaining");
                        System.out.printf("\n Schedule ID %d - Remaining seats: %d%n", inputId, remaining);
                    }
                }

                if (!hasSeat) {
                    System.out.println("No seat data found for this schedule.");
                } else {
                    System.out.printf("Full seat layout for Schedule ID %d:%n", inputId);

                    for (String row : seatMap.keySet()) {
                        List<String> seats = seatMap.get(row);
                        // 숫자 기준 정렬
                        seats.sort(Comparator.comparingInt(s -> Integer.parseInt(s.replaceAll("[^0-9]", ""))));

                        System.out.print(row + " row: ");
                        for (String sn : seats) {
                            if (seatReservedMap.getOrDefault(sn, true)) {
                                System.out.print(" --  ");
                            } else {
                                System.out.print("[" + sn + "] ");
                            }
                        }
                        System.out.println();
                    }
                }

            } catch (SQLException e) {
                System.out.println("Failed to load seat information.");
                e.printStackTrace();
            }


            // 4. Repeat menu
            while (true) {
                System.out.print("\nDo you want to check another schedule? (1: Yes / 0: No): ");
                String input = scanner.nextLine().trim();

                if (input.equals("1")) {
                    break; // 다음 루프로 계속
                } else if (input.equals("0")) {
                    System.out.println("Exiting seat inquiry menu.");
                    return; // 함수 종료
                } else {
                    System.out.println("Invalid input. Please enter 1 (Yes) or 0 (No).");
                }
            }

        }
    }




}
