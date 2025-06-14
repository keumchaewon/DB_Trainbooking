package com.team.project.menu;

import java.util.*;
import java.sql.*;
import com.team.project.ConnectionManager;

public class InsertMenu {

    public static void insertUser(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            String name = InputValidator.getNonEmptyString(scanner, "Enter name: ");
            String phone = InputValidator.getNonEmptyString(scanner, "Enter phone number: ");
            String email = InputValidator.getNonEmptyString(scanner, "Enter email address: ");
            // [수정] 이메일/전화번호 중복 검사
            String checkSql = "SELECT COUNT(*) FROM User WHERE email = ? OR phone = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, phone);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("A user with the same email or phone number already exists.");
                    return;
                }
            }

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
            String train_name = InputValidator.getValidTrainName(scanner, "Enter train name (e.g., KTX-100): ");
            String train_type = InputValidator.getValidTrainType(scanner, "Enter train type");

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
            String start_station = InputValidator.getNonEmptyString(scanner, "Enter start station: ");
            String end_station = InputValidator.getNonEmptyString(scanner, "Enter end station: ");

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
            System.out.println("\n[Available Trains]");
            String trainSql = "SELECT train_id, train_name, train_type FROM Train ORDER BY train_id";
            try (PreparedStatement stmt = conn.prepareStatement(trainSql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.printf("%-5s | %-10s | %-10s%n", "ID", "Train Name", "Type");
                System.out.println("----------------------------------------");
                while (rs.next()) {
                    int id = rs.getInt("train_id");
                    String name = rs.getString("train_name");
                    String type = rs.getString("train_type");

                    // 길이 제한 처리 (15자 이상은 자름)
                    type = (type.length() > 10) ? type.substring(0, 9) + "…" : type;

                    System.out.printf("%-5d | %-10s | %-10s%n", id, name, type);
                }
                System.out.println();
            }

            // [수정] 존재하는 train_id 입력될 때까지 반복
            int train_id;
            while (true) {
                train_id = InputValidator.getValidInt(scanner, "Enter train_id: ");
                String checkTrainSql = "SELECT COUNT(*) FROM Train WHERE train_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkTrainSql)) {
                    checkStmt.setInt(1, train_id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) break;
                }
                System.out.println("Invalid train_id. Please enter an existing train ID.");
            }

            System.out.println("\n[Available Routes]");
            String routeSql = "SELECT route_id, start_station, end_station FROM Route ORDER BY route_id";
            try (PreparedStatement stmt = conn.prepareStatement(routeSql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.printf("%-5s | %-18s -> %-18s%n", "ID", "From", "To");
                System.out.println("--------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-5d | %-18s -> %-18s%n",
                            rs.getInt("route_id"),
                            rs.getString("start_station"),
                            rs.getString("end_station"));
                }
                System.out.println();
            }
            // [수정] 존재하는 route_id 입력될 때까지 반복
            int route_id;
            while (true) {
                route_id = InputValidator.getValidInt(scanner, "Enter route_id: ");
                String checkRouteSql = "SELECT COUNT(*) FROM Route WHERE route_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkRouteSql)) {
                    checkStmt.setInt(1, route_id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) break;
                }
                System.out.println("Invalid route_id. Please enter an existing route ID.");
            }
            java.sql.Date run_date = InputValidator.getValidDate(scanner, "Enter run_date (yyyy-mm-dd): ");
            java.sql.Time departure_time = InputValidator.getValidTime(scanner, "Enter departure_time (HH:mm:ss): ");

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

    public static void insertSeat(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.println("\n[Available Schedules]");
            String scheduleListSql = "SELECT schedule_id, train_id, run_date, departure_time FROM Schedule ORDER BY schedule_id";
            try (PreparedStatement stmt = conn.prepareStatement(scheduleListSql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.printf("%-5s | %-7s | %-12s | %-10s%n",
                        "ID", "TrainID", "Run Date", "Departure");
                System.out.println("----------------------------------------------");
                while (rs.next()) {
                    int id = rs.getInt("schedule_id");
                    int trainId = rs.getInt("train_id");
                    java.sql.Date runDate = rs.getDate("run_date");
                    Time departure = rs.getTime("departure_time");

                    System.out.printf("%-5d | %-7d | %-12s | %-10s%n",
                            id, trainId, runDate.toString(), departure.toString());
                }
                System.out.println();
            }

            int schedule_id;
            while (true) {
                schedule_id = InputValidator.getValidInt(sc, "Enter schedule_id: ");
                String checkScheduleSql = "SELECT COUNT(*) FROM Schedule WHERE schedule_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkScheduleSql)) {
                    checkStmt.setInt(1, schedule_id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) break;
                }
                System.out.println("Invalid schedule_id. Please enter an existing schedule ID.");
            }

            // 기존 좌석 출력
            String seatListSql = "SELECT seat_number FROM Seat WHERE schedule_id = ?";
            try (PreparedStatement seatStmt = conn.prepareStatement(seatListSql)) {
                seatStmt.setInt(1, schedule_id);
                ResultSet rs = seatStmt.executeQuery();

                Map<Integer, List<String>> rowMap = new TreeMap<>();
                while (rs.next()) {
                    String seat = rs.getString("seat_number").toUpperCase();
                    String rowStr = seat.replaceAll("[^0-9]", "");
                    String colChar = seat.replaceAll("[0-9]", "");

                    int rowNum = Integer.parseInt(rowStr);
                    rowMap.computeIfAbsent(rowNum, k -> new ArrayList<>()).add(colChar);
                }

                if (rowMap.isEmpty()) {
                    System.out.println("No seats registered yet for this schedule.");
                } else {
                    System.out.println("\n[Seats already registered for schedule ID " + schedule_id + "]");
                    for (int row : rowMap.keySet()) {
                        List<String> cols = rowMap.get(row);
                        cols.sort(Comparator.naturalOrder());
                        System.out.print(row + " row: ");
                        for (String c : cols) {
                            System.out.print("[" + row + c + "] ");
                        }
                        System.out.println();
                    }
                }
            } catch (SQLException e) {
                System.out.println("Failed to retrieve seats.");
                e.printStackTrace();
            }

            // [수정] 행 수(n)와 열 문자(X)를 입력받아 일괄 등록
            int numRows = InputValidator.getValidRowCount(sc, "Enter number of rows to insert (e.g., 10): ");
            String maxCol = InputValidator.getValidColumn(sc, "Enter last column letter to insert (e.g., D): ").toUpperCase();
            int maxColIndex = maxCol.charAt(0) - 'A';

            int insertedCount = 0, skippedCount = 0;

            String checkSql = "SELECT COUNT(*) FROM Seat WHERE schedule_id = ? AND seat_number = ?";
            String insertSql = "INSERT INTO Seat(schedule_id, seat_number) VALUES (?, ?)";

            for (int row = 1; row <= numRows; row++) {
                for (int i = 0; i <= maxColIndex; i++) {
                    String seat_number = row + String.valueOf((char) ('A' + i));

                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, schedule_id);
                        checkStmt.setString(2, seat_number);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            skippedCount++;
                            continue;
                        }
                    }

                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setInt(1, schedule_id);
                        pstmt.setString(2, seat_number);
                        pstmt.executeUpdate();
                        insertedCount++;
                    }
                }
            }

            System.out.printf("Seat registration completed: %d inserted, %d skipped (already exists)%n",
                    insertedCount, skippedCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertReservation(Scanner scanner) {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            String name = InputValidator.getNonEmptyString(scanner, "Enter name: ");
            String email = InputValidator.getNonEmptyString(scanner, "Enter email: ");
            int userId = -1;

            String checkUserSql = "SELECT user_id FROM User WHERE name = ? AND email = ?";
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                checkUserStmt.setString(1, name);
                checkUserStmt.setString(2, email);
                ResultSet rs = checkUserStmt.executeQuery();

                if (rs.next()) {
                    userId = rs.getInt("user_id");
                } else {
                    System.out.println("No user found with the given name and email.");
                    return;
                }
            }

            try {String scheduleListSql = """
SELECT s.schedule_id, t.train_name, t.train_type, r.start_station, r.end_station, s.run_date, s.departure_time
FROM Schedule s
JOIN Route r ON s.route_id = r.route_id
JOIN Train t ON s.train_id = t.train_id
ORDER BY s.schedule_id
""";

                try (PreparedStatement stmt = conn.prepareStatement(scheduleListSql)) {
                    ResultSet rs = stmt.executeQuery();

                    System.out.println("Train Schedule Overview:");
                    System.out.printf("%-4s | %-10s | %-10s | %-16s | %-16s | %-12s | %-10s%n",
                            "ID", "Train", "Type", "From", "To", "Run Date", "Time");
                    System.out.println("--------------------------------------------------------------------------------------------");

                    while (rs.next()) {
                        int id = rs.getInt("schedule_id");

                        String trainName = rs.getString("train_name");
                        String trainType = rs.getString("train_type");
                        String startStation = rs.getString("start_station");
                        String endStation = rs.getString("end_station");
                        String runDate = rs.getDate("run_date").toString();
                        String departure = rs.getTime("departure_time").toString();

                        // 길이 제한
                        trainName = (trainName.length() > 10) ? trainName.substring(0, 9) + "…" : trainName;
                        trainType = (trainType.length() > 10) ? trainType.substring(0, 9) + "…" : trainType;
                        startStation = (startStation.length() > 16) ? startStation.substring(0, 15) + "…" : startStation;
                        endStation = (endStation.length() > 16) ? endStation.substring(0, 15) + "…" : endStation;

                        // 색상 처리
                        String colorStart = "";
                        String colorEnd = "";
                        if ("express".equalsIgnoreCase(trainType)) {
                            colorStart = "\u001B[34m";  // 파란색
                            colorEnd = "\u001B[0m";     // 리셋
                        }

                        System.out.printf("%-4d | %-10s | %s%-10s%s | %-16s | %-16s | %-1s | %-10s%n",
                                id, trainName, colorStart, trainType, colorEnd, startStation, endStation, runDate, departure);
                    }


                    System.out.println();
                }




                int scheduleId = InputValidator.getValidInt(scanner, "Enter schedule ID: ");

                String checkScheduleSql = "SELECT COUNT(*) FROM Schedule WHERE schedule_id = ?";
                try (PreparedStatement checkScheduleStmt = conn.prepareStatement(checkScheduleSql)) {
                    checkScheduleStmt.setInt(1, scheduleId);
                    ResultSet rs = checkScheduleStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("No schedule found with the given ID.");
                        return;
                    }
                }

                System.out.println("\nAll Seats Overview");
                String showAllSeatsSql = "SELECT seat_number, is_reserved FROM Seat WHERE schedule_id = ?";

                Map<String, List<String>> seatMap = new TreeMap<>();
                Map<String, Boolean> seatReservedMap = new HashMap<>();

                try (PreparedStatement stmt = conn.prepareStatement(showAllSeatsSql)) {
                    stmt.setInt(1, scheduleId);
                    ResultSet rs = stmt.executeQuery();

                    boolean hasSeat = false;

                    while (rs.next()) {
                        hasSeat = true;
                        String seatNumber = rs.getString("seat_number");
                        boolean reserved = rs.getBoolean("is_reserved");
                        String row = seatNumber.replaceAll("[^A-Z]", "");

                        seatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatNumber);
                        seatReservedMap.put(seatNumber, reserved);
                    }

                    if (!hasSeat) {
                        System.out.println("No seat information available.");
                        return;
                    }

                    for (String row : seatMap.keySet()) {
                        System.out.print(row + " row: ");
                        List<String> seats = seatMap.get(row);

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
                }

                String seatNumber = InputValidator.getValidSeatNumber(scanner, "Enter seat number to reserve (e.g., 1A): ").toUpperCase();

                String checkSeatSql = "SELECT is_reserved, seat_id FROM Seat WHERE seat_number = ? AND schedule_id = ?";
                int seatId = -1;
                try (PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatSql)) {
                    checkSeatStmt.setString(1, seatNumber);
                    checkSeatStmt.setInt(2, scheduleId);
                    ResultSet rs = checkSeatStmt.executeQuery();

                    if (!rs.next()) {
                        System.out.println("The selected seat does not exist.");
                        return;
                    }
                    if (rs.getBoolean("is_reserved")) {
                        System.out.println("This seat has already been reserved.");
                        return;
                    }
                    seatId = rs.getInt("seat_id");
                }

                String insertSql = "INSERT INTO Reservation (user_id, schedule_id, seat_id) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, scheduleId);
                    insertStmt.setInt(3, seatId);
                    insertStmt.executeUpdate();
                }

                String updateSql = "UPDATE Seat SET is_reserved = 1 WHERE seat_id = ? AND schedule_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, seatId);
                    updateStmt.setInt(2, scheduleId);
                    updateStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Reservation completed successfully!");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("An error occurred while processing the reservation.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }

}
