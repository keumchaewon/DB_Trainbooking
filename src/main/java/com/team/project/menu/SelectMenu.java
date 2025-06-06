package com.team.project.menu;

import com.team.project.ConnectionManager;
import java.sql.*;
import java.util.Scanner;

public class SelectMenu {
    public static void run(Scanner sc) {
        System.out.println("[View Menu]");
        System.out.println("1. View user list");
        System.out.println("2. View reservation details");
        System.out.println("3. View available seats");
        System.out.println("4. View all reservations");
        System.out.println("5. Search by condition (user name)");
        System.out.print("Select: ");
        int choice = sc.nextInt();
        sc.nextLine();  // 개행 제거

        try (Connection conn = ConnectionManager.getConnection()) {
            switch (choice) {
                case 1 -> showUsers(conn);
                case 2 -> showReservations(conn);
                case 3 -> showRemainingSeats(conn);
                case 4 -> showAllReservations(conn);
                case 5 -> searchReservationByCondition(conn, sc);
                default -> System.out.println("Invalid option");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        String sql = "SELECT * FROM RemainingSeats"; // View 사용
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("[Remaining Seats]");
            while (rs.next()) {
                System.out.printf("seat ID: %d, schedule ID: %d, seat: %s%n",
                        rs.getInt("seat_id"),
                        rs.getInt("schedule_id"),
                        rs.getString("seat_number"));
            }
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
}
