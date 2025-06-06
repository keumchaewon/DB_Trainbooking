package com.team.project.menu;

import com.team.project.ConnectionManager;
import java.sql.*;
import java.util.Scanner;

public class SelectMenu {
    public static void run(Scanner sc) {
        System.out.println("[View Menu]");
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

    public static void showUserReservations(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        int userId = Integer.parseInt(scanner.nextLine());

        String sql = """
        SELECT u.name AS user_name, r.reservation_id, t.train_name, s.run_date, st.seat_number
        FROM Reservation r
        JOIN User u ON r.user_id = u.user_id
        JOIN Schedule s ON r.schedule_id = s.schedule_id
        JOIN Train t ON s.train_id = t.train_id
        JOIN Seat st ON r.seat_id = st.seat_id
        WHERE r.user_id = ?
    """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n📄 [Your Reservations]");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf(" - Name: %s | Train: %s | Date: %s | Seat: %s%n",
                        rs.getString("user_name"),
                        rs.getString("train_name"),
                        rs.getDate("run_date"),
                        rs.getString("seat_number"));
            }

            if (!found) {
                System.out.println("No reservations found for this user.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Failed to load reservations.");
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
            String summarySql = "SELECT schedule_id, COUNT(*) AS remaining_seats " +
                    "FROM Seat WHERE is_reserved = FALSE " +
                    "GROUP BY schedule_id ORDER BY schedule_id";

            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement summaryStmt = conn.prepareStatement(summarySql);
                 ResultSet summaryRs = summaryStmt.executeQuery()) {

                System.out.println("\n📅 [Available Schedules with Remaining Seats]");
                while (summaryRs.next()) {
                    int scheduleId = summaryRs.getInt("schedule_id");
                    int count = summaryRs.getInt("remaining_seats");
                    System.out.printf(" - Schedule ID: %d (Remaining seats: %d)%n", scheduleId, count);
                }

            } catch (SQLException e) {
                System.out.println("❌ Failed to load schedule list.");
                e.printStackTrace();
                return;
            }

            // 2. Ask user to input a schedule ID to see seat details
            System.out.print("\nEnter the Schedule ID you want to check: ");
            int inputId = Integer.parseInt(scanner.nextLine());

            // 3. Show seat details for the selected schedule
            String detailSql = "SELECT seat_id, seat_number FROM Seat " +
                    "WHERE schedule_id = ? AND is_reserved = FALSE";

            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {

                detailStmt.setInt(1, inputId);
                ResultSet detailRs = detailStmt.executeQuery();

                // Also show remaining seat count again
                String countSql = "SELECT COUNT(*) AS remaining FROM Seat WHERE schedule_id = ? AND is_reserved = FALSE";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    countStmt.setInt(1, inputId);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        int remaining = countRs.getInt("remaining");
                        System.out.printf("\n🔎 Schedule ID %d - Remaining seats: %d%n", inputId, remaining);
                    }
                }

                System.out.printf("🪑 Available Seats for Schedule ID %d:%n", inputId);
                boolean found = false;
                while (detailRs.next()) {
                    found = true;
                    System.out.printf(" - Seat ID: %d, Seat Number: %s%n",
                            detailRs.getInt("seat_id"),
                            detailRs.getString("seat_number"));
                }

                if (!found) {
                    System.out.println("❗No available seats for this schedule.");
                }

            } catch (SQLException e) {
                System.out.println("❌ Failed to load seat information.");
                e.printStackTrace();
            }

            // 4. Ask if user wants to check another schedule
            System.out.print("\nDo you want to check another schedule? (1: Yes / 0: No): ");
            int again = Integer.parseInt(scanner.nextLine());
            if (again == 0) {
                System.out.println("Exiting seat inquiry menu.");
                break;
            }
        }
    }



}
