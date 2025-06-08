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
        String sql = "SELECT * FROM UserReservations ORDER BY run_date ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("[Reservation List]");
            System.out.printf("%-12s | %-10s | %-20s | %-10s%n", "Run Date", "Train", "Name", "Seat");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                String runDate = rs.getDate("run_date").toString();
                String train = rs.getString("train_name");
                String name = rs.getString("user_name");
                String seat = rs.getString("seat_number");

                // 문자열이 길면 줄이기
                //name = (name.length() > 10) ? name.substring(0, 9) + "…" : name;
                //train = (train.length() > 15) ? train.substring(0, 14) + "…" : train;

                System.out.printf("%-12s | %-10s | %-20s | %-10s%n",
                        runDate, train, name, seat);
            }

            System.out.println();
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

            System.out.println("List of Available Train Schedules:");
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
        SELECT u.name AS user_name, r.reservation_id, t.train_name, t.train_type, 
               s.run_date, st.seat_number
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
                String userName = rs.getString("user_name");
                String trainName = rs.getString("train_name");
                String trainType = rs.getString("train_type");
                String runDate = rs.getDate("run_date").toString();
                String seatNumber = rs.getString("seat_number");

                // express면 파란색 처리
                String colorStart = "";
                String colorEnd = "";
                if ("express".equalsIgnoreCase(trainType)) {
                    colorStart = "\u001B[34m";
                    colorEnd = "\u001B[0m";
                }

                System.out.printf("name: %-10s | train: %-12s | type: %s%-10s%s | date: %s | seat: %s%n",
                        userName, trainName, colorStart, trainType, colorEnd, runDate, seatNumber);
            }
        }
    }


    public static void showUserReservations(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        String sql = """
        SELECT u.name AS user_name, t.train_name, t.train_type, s.run_date, st.seat_number
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
                String trainName = rs.getString("train_name");
                String trainType = rs.getString("train_type");
                String runDate = rs.getDate("run_date").toString();
                String seatNumber = rs.getString("seat_number");

                // express면 파란색 처리
                String colorStart = "";
                String colorEnd = "";
                if ("express".equalsIgnoreCase(trainType)) {
                    colorStart = "\u001B[34m";
                    colorEnd = "\u001B[0m";
                }

                System.out.printf(" - Train: %-12s | Type: %s%-10s%s | Date: %s | Seat: %s%n",
                        trainName, colorStart, trainType, colorEnd, runDate, seatNumber);
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
            String scheduleListSql = """
SELECT s.schedule_id, t.train_name, t.train_type, r.start_station, r.end_station,
       s.run_date, s.departure_time, COUNT(seat.seat_id) AS remaining_seats
FROM Schedule s
JOIN Train t ON s.train_id = t.train_id
JOIN Route r ON s.route_id = r.route_id
LEFT JOIN Seat seat ON s.schedule_id = seat.schedule_id AND seat.is_reserved = FALSE
GROUP BY s.schedule_id, t.train_name, t.train_type, r.start_station, r.end_station, s.run_date, s.departure_time
ORDER BY s.schedule_id
""";

            try (
                    Connection conn = ConnectionManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(scheduleListSql);
                    ResultSet rs = stmt.executeQuery()
            ) {
                System.out.println("Current Schedule List:");
                System.out.printf("%-5s | %-10s | %-10s | %-16s | %-16s | %-12s | %-10s | %-15s%n",
                        "ID", "Train", "Type", "From", "To", "Run Date", "Time", "Available Seats");
                System.out.println("-------------------------------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("schedule_id");
                    String trainName = rs.getString("train_name");
                    String trainType = rs.getString("train_type");
                    String from = rs.getString("start_station");
                    String to = rs.getString("end_station");
                    String runDate = rs.getDate("run_date").toString();
                    String time = rs.getTime("departure_time").toString();
                    int remaining = rs.getInt("remaining_seats");

                    // 16자 초과시 자르기
                    trainType = (trainType.length() > 10) ? trainType.substring(0, 9) + "…" : trainType;
                    from = (from.length() > 16) ? from.substring(0, 15) + "…" : from;
                    to = (to.length() > 16) ? to.substring(0, 15) + "…" : to;

                    // express면 파란색 처리
                    String colorStart = "";
                    String colorEnd = "";
                    if ("express".equalsIgnoreCase(trainType)) {
                        colorStart = "\u001B[34m";  // 파란색
                        colorEnd = "\u001B[0m";     // 리셋
                    }

                    System.out.printf("%-5d | %-10s | %s%-10s%s | %-16s | %-16s | %-12s | %-10s | %-15d%n",
                            id, trainName, colorStart, trainType, colorEnd, from, to, runDate, time, remaining);
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

    public static void viewReservedSeatInfo(Scanner scanner) {
        // 1. 조회 방식 선택
        System.out.println("\n[Search Mode]");
        System.out.println("1. Search by Run Date");
        System.out.println("2. Search by Train Name");
        System.out.println("3. Search by Schedule ID");
        System.out.print("Select search mode: ");

        String searchMode = scanner.nextLine();
        String scheduleSql = "";

        switch (searchMode) {
            case "1" -> {
                String date;
                while (true) {
                    System.out.print("Enter run date (yyyy-mm-dd): ");
                    date = scanner.nextLine().trim();
                    if (date.matches("\\d{4}-\\d{2}-\\d{2}")) break;
                    System.out.println("Invalid date format. Please enter yyyy-mm-dd.");
                }
                scheduleSql = """
                SELECT s.schedule_id, t.train_name, r.start_station, r.end_station, s.run_date, s.departure_time
                FROM Schedule s
                JOIN Train t ON s.train_id = t.train_id
                JOIN Route r ON s.route_id = r.route_id
                WHERE s.run_date = '""" + date + "' ORDER BY s.schedule_id";
            }
            case "2" -> {
                System.out.print("Enter train name (partial allowed): ");
                String train = scanner.nextLine().trim();
                scheduleSql = """
                SELECT s.schedule_id, t.train_name, r.start_station, r.end_station, s.run_date, s.departure_time
                FROM Schedule s
                JOIN Train t ON s.train_id = t.train_id
                JOIN Route r ON s.route_id = r.route_id
                WHERE t.train_name LIKE '%""" + train + "%' ORDER BY s.schedule_id";
            }
            case "3" -> {
                scheduleSql = """
                SELECT s.schedule_id, t.train_name, r.start_station, r.end_station, s.run_date, s.departure_time
                FROM Schedule s
                JOIN Train t ON s.train_id = t.train_id
                JOIN Route r ON s.route_id = r.route_id
                ORDER BY s.schedule_id""";
            }
            default -> {
                System.out.println("Invalid input. Returning to menu.");
                return;
            }
        }

        List<Integer> validScheduleIds = new ArrayList<>();

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(scheduleSql);
                ResultSet rs = stmt.executeQuery()
        ) {
            System.out.println("\n[Train Schedules]");
            System.out.printf("%-5s | %-10s | %-20s | %-20s | %-12s | %-10s%n",
                    "ID", "Train", "From", "To", "Run Date", "Time");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("schedule_id");
                validScheduleIds.add(id);
                System.out.printf("%-5d | %-10s | %-20s | %-20s | %-12s | %-10s%n",
                        id,
                        rs.getString("train_name"),
                        rs.getString("start_station"),
                        rs.getString("end_station"),
                        rs.getDate("run_date").toString(),
                        rs.getTime("departure_time").toString());
            }
            if (validScheduleIds.isEmpty()) {
                System.out.println("No matching schedules found.");
                return;
            }

        } catch (SQLException e) {
            System.out.println("Failed to load schedules.");
            e.printStackTrace();
            return;
        }

        // 2. 스케줄 ID 입력 및 유효성 검사
        int scheduleId;
        while (true) {
            System.out.print("\nEnter a schedule ID to view reserved seats: ");
            String input = scanner.nextLine();
            try {
                scheduleId = Integer.parseInt(input);
                if (!validScheduleIds.contains(scheduleId)) {
                    System.out.println("Invalid schedule ID. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // 3. 예약된 좌석 조회 및 출력
        String reservedSeatSql = "SELECT seat_number FROM Seat WHERE schedule_id = ? AND is_reserved = TRUE";
        Map<String, List<String>> reservedSeatMap = new TreeMap<>();

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(reservedSeatSql)
        ) {
            stmt.setInt(1, scheduleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String seatNumber = rs.getString("seat_number");
                String row = seatNumber.replaceAll("[^A-Z]", "");
                reservedSeatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatNumber);
            }

            if (reservedSeatMap.isEmpty()) {
                System.out.println("No reservations found for this schedule.");
                return;
            }

            System.out.printf("\n[Reserved Seats for Schedule ID %d]\n", scheduleId);
            for (String row : reservedSeatMap.keySet()) {
                List<String> seats = reservedSeatMap.get(row);
                seats.sort(Comparator.comparingInt(s -> Integer.parseInt(s.replaceAll("[^0-9]", ""))));
                System.out.print(row + " row: ");
                for (String seat : seats) {
                    System.out.print("[" + seat + "] ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Failed to load reserved seats.");
            e.printStackTrace();
            return;
        }

        // 4. 좌석 번호 입력 후 예약 정보 조회
        System.out.print("\nEnter seat number to view reservation info: ");
        String seatInput = scanner.nextLine().trim().toUpperCase();

        String seatIdSql = "SELECT seat_id FROM Seat WHERE schedule_id = ? AND seat_number = ?";
        int seatId = -1;
        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement seatStmt = conn.prepareStatement(seatIdSql)
        ) {
            seatStmt.setInt(1, scheduleId);
            seatStmt.setString(2, seatInput);
            ResultSet seatRs = seatStmt.executeQuery();

            if (seatRs.next()) {
                seatId = seatRs.getInt("seat_id");
            } else {
                System.out.println("Invalid seat number for this schedule.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve seat ID.");
            e.printStackTrace();
            return;
        }

        String reservationDetailSql = """
        SELECT u.name AS user_name, u.email, s.run_date, t.train_name, st.seat_number
        FROM Reservation r
        JOIN User u ON r.user_id = u.user_id
        JOIN Schedule s ON r.schedule_id = s.schedule_id
        JOIN Train t ON s.train_id = t.train_id
        JOIN Seat st ON r.seat_id = st.seat_id
        WHERE r.schedule_id = ? AND r.seat_id = ?
    """;

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(reservationDetailSql)
        ) {
            stmt.setInt(1, scheduleId);
            stmt.setInt(2, seatId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n[Reservation Details]");
                System.out.println("User: " + rs.getString("user_name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Train: " + rs.getString("train_name"));
                System.out.println("Run Date: " + rs.getDate("run_date"));
                System.out.println("Seat: " + rs.getString("seat_number"));
            } else {
                System.out.println("No reservation found for this seat.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve reservation information.");
            e.printStackTrace();
        }
    }




}
