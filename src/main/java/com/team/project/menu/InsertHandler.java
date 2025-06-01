package train;
import java.sql.*;
import java.util.Scanner;
public class InsertHandler {
    public static void insertUser(Scanner scanner) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.print("Enter user id:");
            int user_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter name:");
            String name = scanner.nextLine();

            System.out.print("Enter phone number:");
            String phone = scanner.nextLine();

            System.out.print("Enter email address:");
            String email = scanner.nextLine();

            String sql = "INSERT INTO User(user_id, name, phone, email) VALUES (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            System.out.println("User inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertTrain(Scanner scanner) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.print("Enter train_id:");
            int train_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter train_name:");
            String train_name = scanner.nextLine();

            System.out.print("Enter train_type:");
            String train_type = scanner.nextLine();

            String sql = "INSERT INTO Train(train_id, train_name, train_type) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, train_id);
            pstmt.setString(2, train_name);
            pstmt.setString(3, train_type);
            pstmt.executeUpdate();
            System.out.println("Train inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRoute(Scanner scanner) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.print("Enter route_id:");
            int route_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter start_station:");
            String start_station = scanner.nextLine();

            System.out.print("Enter end_station:");
            String end_station = scanner.nextLine();

            String sql = "INSERT INTO Route(route_id, start_station, end_station) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, route_id);
            pstmt.setString(2, start_station);
            pstmt.setString(3, end_station);
            pstmt.executeUpdate();
            System.out.println("Route inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSchedule(Scanner scanner) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.print("Enter schedule_id:");
            int schedule_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter train_id:");
            int train_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter route_id:");
            int route_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter run_date (yyyy-mm-dd):");
            Date run_date = Date.valueOf(scanner.nextLine());

            System.out.print("Enter departure_time (HH:mm:ss):");
            Time departure_time = Time.valueOf(scanner.nextLine());


            String sql = "INSERT INTO Schedule(schedule_id,train_id, route_id, run_date, departure_time) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, schedule_id);
            pstmt.setInt(2, train_id);
            pstmt.setInt(3, route_id);
            pstmt.setDate(4, run_date);
            pstmt.setTime(5, departure_time);
            pstmt.executeUpdate();
            System.out.println("Route inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertSeat(Scanner scanner) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.print("Enter seat_id:");
            int seat_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter schedule_id:");
            int schedule_id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter seat_number:");
            String seat_number = scanner.nextLine();

            System.out.print("Enter is_reserved:");
            Boolean is_reserved = Boolean.parseBoolean(scanner.nextLine());

            String sql = "INSERT INTO Seat(seat_id, schedule_id, seat_number, is_reserved) VALUES (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, seat_id);
            pstmt.setInt(2, schedule_id);
            pstmt.setString(3, seat_number);
            pstmt.setBoolean(4, is_reserved);
            pstmt.executeUpdate();
            System.out.println("Route inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertReservation(Scanner scanner) { //transaction
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                System.out.print("Enter reservation ID: ");
                int reservationId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter user ID: ");
                int userId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter schedule ID: ");
                int scheduleId = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter seat ID: ");
                int seatId = Integer.parseInt(scanner.nextLine());

                // 1. 예약 삽입
                String insertSql = "INSERT INTO Reservation (reservation_id, user_id, schedule_id, seat_id) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, reservationId);
                insertStmt.setInt(2, userId);
                insertStmt.setInt(3, scheduleId);
                insertStmt.setInt(4, seatId);
                insertStmt.executeUpdate();

                // 2. 좌석 예약 상태 변경
                String updateSql = "UPDATE Seat SET is_reserved = TRUE WHERE seat_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, seatId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("Reservation inserted and seat marked as reserved.");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction rolled back due to error.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

