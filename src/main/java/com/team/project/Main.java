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
}
