package com.team.project.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.team.project.ConnectionManager;

import java.sql.*;

public class DeleteMenu {

    // 1. 예약 취소의 경우
    public static void deleteReservation(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            deleteReservation(conn, sc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);

        try (
                PreparedStatement findUser = conn.prepareStatement("SELECT user_id FROM user WHERE name = ? AND email = ?");
                PreparedStatement findReservations = conn.prepareStatement(
                        "SELECT r.reservation_id, s.seat_id, s.seat_number, ro.start_station, ro.end_station, sch.run_date " +
                                "FROM reservation r " +
                                "JOIN seat s ON r.seat_id = s.seat_id " +
                                "JOIN schedule sch ON r.schedule_id = sch.schedule_id " +
                                "JOIN route ro ON sch.route_id = ro.route_id " +
                                "WHERE r.user_id = ?"
                );
                PreparedStatement delRes = conn.prepareStatement("DELETE FROM reservation WHERE reservation_id = ?");
                PreparedStatement freeSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 0 WHERE seat_id = ?");
        ) {
            // 사용자 입력
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

            // 사용자 예약 목록 출력
            findReservations.setInt(1, userId);
            ResultSet resRs = findReservations.executeQuery();

            System.out.println("\n[Your Reservations]");
            Map<Integer, Integer> resToSeatMap = new HashMap<>();
            int count = 0;

            while (resRs.next()) {
                int resId = resRs.getInt("reservation_id");
                int seatId = resRs.getInt("seat_id");
                String seatNum = resRs.getString("seat_number");
                String start = resRs.getString("start_station");
                String end = resRs.getString("end_station");
                Date date = resRs.getDate("run_date");

                System.out.printf("Reservation ID: %d | Seat: %s | %s → %s | Date: %s\n", resId, seatNum, start, end, date);
                resToSeatMap.put(resId, seatId);
                count++;
            }

            if (count == 0) {
                System.out.println("No reservations found.");
                return;
            }

            // 삭제할 예약 선택
            System.out.print("Enter Reservation ID to cancel: ");
            int resIdToCancel = Integer.parseInt(sc.nextLine());

            Integer seatIdToFree = resToSeatMap.get(resIdToCancel);
            if (seatIdToFree == null) {
                System.out.println("Invalid reservation ID.");
                conn.rollback();
                return;
            }

            delRes.setInt(1, resIdToCancel);
            freeSeat.setInt(1, seatIdToFree);
            delRes.executeUpdate();
            freeSeat.executeUpdate();

            conn.commit();
            System.out.println("Reservation cancelled successfully.");

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Error occurred during cancellation.");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }


    //2. 사용자 삭제의 경우
    // 2. 사용자 삭제의 경우
    public static void deleteUser(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            deleteUser(conn, sc);  // 내부 private 메서드 호출
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 내부 처리용 private 메서드
    private static void deleteUser(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter user name to delete: ");
        String name = sc.nextLine();
        System.out.print("Enter user email to confirm: ");
        String email = sc.nextLine();

        String sql = "DELETE FROM user WHERE name = ? AND email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            int deleted = pstmt.executeUpdate();
            System.out.println(deleted > 0 ? "User deleted successfully." : "User not found or email mismatch.");
        }
    }
}

