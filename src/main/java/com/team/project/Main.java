package com.team.project;

import com.team.project.menu.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== 메인 메뉴 ===");
                System.out.println("1. 등록");
                System.out.println("2. 수정");
                System.out.println("3. 삭제");
                System.out.println("4. 조회");
                System.out.println("0. 종료");
                System.out.print("번호 입력: ");
                int choice = sc.nextInt();
                sc.nextLine();  // 버퍼 비우기

                switch (choice) {
                    case 1 -> InsertMenu.run(sc);
                    case 2 -> UpdateMenu.run(sc);
                    case 3 -> DeleteMenu.run(sc);
                    case 4 -> SelectMenu.run(sc);
                    case 0 -> {
                        System.out.println("종료합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            }
        }
    }

    public static class InsertMenu {
        public static void run(Scanner scanner) {
            while (true) {
                System.out.println("\n--- 등록 메뉴 ---");
                System.out.println("1. 사용자 등록");
                System.out.println("2. 열차 등록");
                System.out.println("3. 노선 등록");
                System.out.println("4. 스케줄 등록");
                System.out.println("5. 좌석 등록");
                System.out.println("6. 예약 등록");
                System.out.println("0. 메인 메뉴로 돌아가기");
                System.out.print("선택: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> InsertMenu.insertUser(scanner);
                    case "2" -> InsertMenu.insertTrain(scanner);
                    case "3" -> InsertMenu.insertRoute(scanner);
                    case "4" -> InsertMenu.insertSchedule(scanner);
                    case "5" -> InsertMenu.insertSeat(scanner);
                    case "6" -> InsertMenu.insertReservation(scanner);
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }

    public static class UpdateMenu {
        public static void run(Scanner scanner) {
            while (true) {
                System.out.println("\n--- 수정 메뉴 ---");
                System.out.println("1. 사용자 정보 수정");
                System.out.println("2. 예약 정보 수정");
                System.out.println("0. 메인 메뉴로 돌아가기");
                System.out.print("선택: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> UpdateMenu.updateUserInfo(scanner);
                    case "2" -> UpdateMenu.updateReservation(scanner);
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }
}
