package com.team.project;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                // 메인 메뉴
                System.out.println("\n=== 기차 예매 시스템 ===");
                System.out.println("1. 기차표 예매하기");
                System.out.println("2. 예약 조회/수정");
                System.out.println("3. 예약 취소");
                System.out.println("4. 회원 정보 관리");
                System.out.println("0. 종료");
                System.out.print("번호 입력: ");
                int choice = sc.nextInt();
                sc.nextLine();  // 버퍼 비우기

                switch (choice) {
                    case 1 -> BookingMenu.run(sc);         // 기차표 예매
                    case 2 -> ReservationMenu.run(sc);     // 예약 조회 및 수정
                    case 3 -> CancelMenu.run(sc);          // 예약 취소
                    case 4 -> UserMenu.run(sc);            // 회원 정보 관리
                    case 0 -> {
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            }
        }
    }

    // 기차표 예매
    public static class BookingMenu {
        public static void run(Scanner scanner) {
            System.out.println("\n=== 기차표 예매 ===");
            System.out.print("출발지 입력: ");
            String departure = scanner.nextLine();
            System.out.print("도착지 입력: ");
            String destination = scanner.nextLine();

            // 열차 선택
            System.out.println("가능한 열차 목록을 조회합니다...");
            System.out.print("원하는 열차 번호 선택: ");
            String trainChoice = scanner.nextLine();

            // 좌석 선택
            System.out.println("가능한 좌석을 조회합니다...");
            System.out.print("원하는 좌석 번호 선택: ");
            String seatChoice = scanner.nextLine();

            // 예매 완료
            System.out.println("기차표 예매가 완료되었습니다.");
        }
    }

    // 예약 조회/수정
    public static class ReservationMenu {
        public static void run(Scanner scanner) {
            System.out.println("\n=== 예약 조회/수정 ===");
            System.out.print("예약 ID 입력: ");
            String reservationId = scanner.nextLine();

            // 예약 정보 조회
            System.out.println("예약 정보를 조회합니다...");
            System.out.print("수정할 항목 선택 (1. 출발지, 2. 도착지, 3. 좌석): ");
            String choice = scanner.nextLine();

            // 수정 진행
            if (choice.equals("1")) {
                System.out.print("새로운 출발지 입력: ");
                String newDeparture = scanner.nextLine();
                System.out.println("출발지가 수정되었습니다.");
            } else if (choice.equals("2")) {
                System.out.print("새로운 도착지 입력: ");
                String newDestination = scanner.nextLine();
                System.out.println("도착지가 수정되었습니다.");
            } else if (choice.equals("3")) {
                System.out.print("새로운 좌석 번호 입력: ");
                String newSeat = scanner.nextLine();
                System.out.println("좌석이 수정되었습니다.");
            } else {
                System.out.println("잘못된 선택입니다.");
            }
        }
    }

    // 예약 취소
    public static class CancelMenu {
        public static void run(Scanner scanner) {
            System.out.println("\n=== 예약 취소 ===");
            System.out.print("취소할 예약 ID 입력: ");
            String reservationId = scanner.nextLine();

            // 예약 취소 진행
            System.out.println("예약이 취소되었습니다.");
        }
    }

    // 회원 정보 관리
    public static class UserMenu {
        public static void run(Scanner scanner) {
            while (true) {
                System.out.println("\n=== 회원 정보 관리 ===");
                System.out.println("1. 회원 정보 조회");
                System.out.println("2. 회원 정보 수정");
                System.out.println("0. 메인 메뉴로 돌아가기");
                System.out.print("선택: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> {
                        System.out.println("회원 정보를 조회합니다...");
                    }
                    case "2" -> {
                        System.out.println("회원 정보를 수정합니다...");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }
}
