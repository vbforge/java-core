package com.vbforge.practice.practice_8_classic_switch;

/**
 * Classic switch (colon form) on an int: consecutive labels, sparse labels, default.
 * Each case returns, so no break is needed here.
 */
public class SwitchOnIntDemo {

    public static void main(String[] args) {
        System.out.println("--- consecutive labels ---");
        System.out.println("choice 3 -> " + operation(3));
        System.out.println("choice 1 -> " + operation(1));
        System.out.println("choice 9 -> " + operation(9));

        System.out.println("\n--- sparse labels, default -> Amateur ---");
        System.out.println("100 -> " + typeOf(100));
        System.out.println("60  -> " + typeOf(60));
        System.out.println("10  -> " + typeOf(10));
        System.out.println("99  -> " + typeOf(99));
    }

    static String operation(int choice) {
        switch (choice) {
            case 1:  return "Creating...";
            case 2:  return "Reading...";
            case 3:  return "Updating...";
            case 4:  return "Deleting...";
            default: return "Invalid input";
        }
    }

    static String typeOf(int points) {
        switch (points) {
            case 100: return "World Class";
            case 60:  return "Country";
            case 10:  return "Region";
            default:  return "Amateur";
        }
    }
}
