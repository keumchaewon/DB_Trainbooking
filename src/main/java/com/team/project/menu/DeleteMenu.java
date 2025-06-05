package com.team.project.menu;

import java.util.Scanner;

import com.team.project.ConnectionManager;

import java.sql.*;

public class DeleteMenu {

    // 1. 예약 취소의 경우
    public static void deleteReservation(Scanner sc) {
        try (Connection conn = ConnectionManager.getConnection()) {
            deleteReservation(conn, sc);  // 내부 private 메서드 호출
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 내부 로직 담당 private 메서드
    private static void deleteReservation(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);

        try (
                PreparedStatement findUser = conn.prepareStatement("SELECT user_id FROM user WHERE name = ?");
                PreparedStatement findReservations = conn.prepareStatement(
                        "SELECT r.reservation_id, s.seat_id, s.seat_number " +
                                "FROM reservation r JOIN seat s ON r.seat_id = s.seat_id " +
                                "WHERE r.user_id = ?"
                );
                PreparedStatement delRes = conn.prepareStatement("DELETE FROM reservation WHERE reservation_id = ?");
                PreparedStatement freeSeat = conn.prepareStatement("UPDATE seat SET is_reserved = 0 WHERE seat_id = ?");
        ) {
            System.out.print("Enter your name: ");
            String name = sc.next();

            // 사용자 ID 찾기
            findUser.setString(1, name);
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
            int count = 0;
            while (resRs.next()) {
                int resId = resRs.getInt("reservation_id");
                String seatNum = resRs.getString("seat_number");
                System.out.printf("Reservation ID: %d | Seat: %s\n", resId, seatNum);
                count++;
            }

            if (count == 0) {
                System.out.println("No reservations found.");
                return;
            }

            // 삭제할 예약 선택
            System.out.print("Enter Reservation ID to cancel: ");
            int resIdToCancel = sc.nextInt();

            // seat_id 알아오기
            findReservations.setInt(1, userId);
            ResultSet rs = findReservations.executeQuery();
            Integer seatIdToFree = null;

            while (rs.next()) {
                if (rs.getInt("reservation_id") == resIdToCancel) {
                    seatIdToFree = rs.getInt("seat_id");
                    break;
                }
            }

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
        String name = sc.next();

        String sql = "DELETE FROM user WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            int deleted = pstmt.executeUpdate();
            System.out.println(deleted > 0 ? "User deleted successfully." : "User not found.");
        }
    }
}

