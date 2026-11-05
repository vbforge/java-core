package com.vbforge.practice.practice_3_if_else_chain;

/**
 * if / else if / else - the classic chain.
 *   - Branches are tried top to bottom; the first true branch wins.
 *   - Order the branches from most-specific to least-specific.
 *   - A plain if / else is the two-branch special case of the same idea.
 */
public class IfElseChainDemo {

    public static void main(String[] args) {
        System.out.println("--- grade calculator ---");
        System.out.println("90 -> " + grade(90));
        System.out.println("85 -> " + grade(85));
        System.out.println("70 -> " + grade(70));
        System.out.println("50 -> " + grade(50));

        System.out.println("\n--- weather chain ---");
        System.out.println(describeWeather("rainy"));
        System.out.println(describeWeather("sunny"));
        System.out.println(describeWeather("windy"));
        System.out.println(describeWeather("snowy"));

        System.out.println("\n--- trivial if / else ---");
        int waterTap = 15;
        int pipeVolume = 25;
        if (waterTap < pipeVolume) {
            System.out.println("flow allowed");
        } else {
            System.out.println("flow blocked");
        }
    }

    static String grade(int marks) {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else return "D or F";
    }

    static String describeWeather(String weather) {
        if (weather.equals("rainy")) return "It's rainy - bring an umbrella.";
        else if (weather.equals("sunny")) return "It's sunny - bring sunglasses.";
        else if (weather.equals("windy")) return "It's windy - bring a coat.";
        else return "Enjoy the snow!";
    }
}
