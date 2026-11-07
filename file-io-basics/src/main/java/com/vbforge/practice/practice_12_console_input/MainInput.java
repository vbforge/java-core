package com.vbforge.practice.practice_12_console_input;

import java.util.Scanner;

public class MainInput {
    public static void main(String[] args) {

        String str = new String("Type here (when you finish typing text, enter number): ");
        System.out.println(str);

        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        int num = scanner.nextInt();
        System.out.println("Your text is: " + s);
        System.out.println("and number = " + num);
    }
}