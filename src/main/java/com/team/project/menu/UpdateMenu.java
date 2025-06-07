package com.team.project.menu;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.team.project.ConnectionManager;

public class UpdateMenu {

    //1. 사용자 정보 편집
    public static void updateUserInfo(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            updateUserInfo(conn, sc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateUserInfo(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter your current name: ");
        String name = sc.nextLine();
        System.out.print("Enter your current email: ");
        String email = sc.nextLine();

        String findSql = "SELECT user_id FROM user WHERE name = ? AND email = ?";
        int userId;

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setString(1, name);
            findStmt.setString(2, email);
          
            ResultSet rs = findStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("User not found.");
                return;
            }
            userId = rs.getInt("user_id");
        }

        // 메뉴 출력
        System.out.println("\nWhich information do you want to update?");
        System.out.println("1. Name");
        System.out.println("2. Phone number");
        System.out.println("3. Email");
        System.out.print("Select option: ");
        int option = Integer.parseInt(sc.nextLine());

        String updateSql = null;
        String newValue = null;
        String columnName = null;

        switch (option) {
            case 1 -> {
                System.out.print("Enter new name: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET name = ? WHERE user_id = ?";
                columnName = "Name";
            }
            case 2 -> {
                System.out.print("Enter new phone number: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET phone = ? WHERE user_id = ?";
                columnName = "Phone number";
            }
            case 3 -> {
                System.out.print("Enter new email: ");
                newValue = sc.nextLine();
                updateSql = "UPDATE user SET email = ? WHERE user_id = ?";
                columnName = "Email";
            }
            default -> {
                System.out.println("Invalid selection.");
                return;
            }
        }

        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newValue);
            updateStmt.setInt(2, userId);
            int updated = updateStmt.executeUpdate();

            if (updated > 0) {
                System.out.println(columnName + " updated successfully!");

                // 변경된 정보 출력
                String selectUpdated = "SELECT name, phone, email FROM user WHERE user_id = ?";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectUpdated)) {
                    selectStmt.setInt(1, userId);
                    ResultSet updatedRs = selectStmt.executeQuery();
                    if (updatedRs.next()) {
                        System.out.println("\n[Updated User Info]");
                        System.out.println("Name : " + updatedRs.getString("name"));
                        System.out.println("Phone: " + updatedRs.getString("phone"));
                        System.out.println("Email: " + updatedRs.getString("email"));
                    }
                }

            } else {
                System.out.println("Update failed.");
            }
        }
    }


    // 2. 사용자 예약 변경
    public static void updateReservationSeat(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement findUser = conn.prepareStatement("SELECT user_id FROM user WHERE name = ? AND email = ?");
                    PreparedStatement findReservations = conn.prepareStatement(
                            "SELECT r.reservation_id, s.seat_number, ro.start_station, ro.end_station, sch.run_date, r.schedule_id " +
                                    "FROM reservation r " +
                                    "JOIN seat s ON r.seat_id = s.seat_id " +
                                    "JOIN schedule sch ON r.schedule_id = sch.schedule_id " +
                                    "JOIN route ro ON sch.route_id = ro.route_id " +
                                    "WHERE r.user_id = ?"
                    );
                    PreparedStatement getSeatId = conn.prepareStatement("SELECT seat_id FROM seat WHERE seat_number = ? AND schedule_id = ?");
                    PreparedStatement freeOldSeat = conn.prepareStatement(
                            "UPDATE seat SET is_reserved = 0 WHERE seat_id = (SELECT seat_id FROM reservation WHERE reservation_id = ?)"
                    );
                    PreparedStatement updateRes = conn.prepareStatement("UPDATE reservation SET seat_id = ? WHERE reservation_id = ?");
                    PreparedStatement markSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 1 WHERE seat_id = ?");
                    PreparedStatement getSeats = conn.prepareStatement("SELECT seat_number, is_reserved FROM seat WHERE schedule_id = ?")
            ) {
                System.out.print("Enter your name: ");
                String name = sc.nextLine();
                System.out.print("Enter your email: ");
                String email = sc.nextLine();


                findUser.setString(1, name);
                findUser.setString(2, email);
                ResultSet userRs = findUser.executeQuery();

                if (!userRs.next()) {
                    System.out.println("User not found.");
                    return;
                }
                int userId = userRs.getInt("user_id");
                findReservations.setInt(1, userId);
                ResultSet resRs = findReservations.executeQuery();
                Map<Integer, Integer> reservationToScheduleMap = new HashMap<>();
                System.out.println("\n[Your Reservations]");
                int count = 0;
                while (resRs.next()) {
                    int resId = resRs.getInt("reservation_id");
                    String seatNum = resRs.getString("seat_number");
                    String start = resRs.getString("start_station");
                    String end = resRs.getString("end_station");
                    Date date = resRs.getDate("run_date");
                    int scheduleId = resRs.getInt("schedule_id");

                    reservationToScheduleMap.put(resId, scheduleId);

                    System.out.printf("Reservation ID: %d | Seat: %s | %s → %s | Date: %s\n",
                            resId, seatNum, start, end, date);
                    count++;
                }

                if (count == 0) {
                    System.out.println("No reservations found.");
                    return;
                }

                System.out.print("Enter Reservation ID to update: ");
                int resId = Integer.parseInt(sc.nextLine());

                if (!reservationToScheduleMap.containsKey(resId)) {
                    System.out.println("Invalid reservation ID.");
                    return;
                }

                int scheduleId = reservationToScheduleMap.get(resId);

                // Show seat map
                getSeats.setInt(1, scheduleId);
                ResultSet seatRs = getSeats.executeQuery();

                Map<String, List<String>> seatMap = new TreeMap<>();
                Map<String, Boolean> seatReservedMap = new HashMap<>();
                boolean hasSeat = false;

                while (seatRs.next()) {
                    hasSeat = true;
                    String seatNumber = seatRs.getString("seat_number");
                    boolean reserved = seatRs.getBoolean("is_reserved");
                    String row = seatNumber.replaceAll("[^A-Z]", "");

                    seatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatNumber);
                    seatReservedMap.put(seatNumber, reserved);
                }

                if (!hasSeat) {
                    System.out.println("No seat information available.");
                    return;
                }

                System.out.println("\nAll Seats Overview");
                for (String row : seatMap.keySet()) {
                    System.out.print(row + "row : ");
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
                String newSeatNumber = InputValidator.getValidSeatNumber(sc, "\nEnter new Seat Number (e.g., 1A): ");

                getSeatId.setString(1, newSeatNumber);
                getSeatId.setInt(2, scheduleId);
                ResultSet newSeatRs = getSeatId.executeQuery();

                if (!newSeatRs.next()) {
                    System.out.println("Seat not found.");
                    conn.rollback();
                    return;
                }

                int newSeatId = newSeatRs.getInt("seat_id");

                freeOldSeat.setInt(1, resId);
                freeOldSeat.executeUpdate();

                updateRes.setInt(1, newSeatId);
                updateRes.setInt(2, resId);
                updateRes.executeUpdate();

                markSeat.setInt(1, newSeatId);
                markSeat.executeUpdate();

                conn.commit();
                System.out.println("Seat change completed.");

            } catch (Exception e) {
                conn.rollback();
                System.out.println("Error occurred during seat update.");
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
