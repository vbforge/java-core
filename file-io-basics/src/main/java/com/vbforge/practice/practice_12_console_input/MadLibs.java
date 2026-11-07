package com.vbforge.practice.practice_12_console_input;

import java.util.Scanner;

public class MadLibs {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a noun: ");
        String noun = scanner.nextLine();

        System.out.println("Enter a verb: ");
        String verb = scanner.nextLine();

        System.out.println("Enter an adjective: ");
        String adjective = scanner.nextLine();

        System.out.println("Enter an adverb: ");
        String adverb = scanner.nextLine();

        System.out.printf("Roses are %s,\nViolets are %s,\nSugar is %s,\nAnd I %s you!\n",
                adjective, noun, adjective, adverb);
        scanner.close();
    }
}