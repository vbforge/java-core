package com.vbforge.practice.practice_8_classic_switch;

/** default can reject unknown input by throwing instead of returning a value. */
public class SwitchDefaultThrowDemo {

    public static void main(String[] args) {
        System.out.println("admin -> " + accessLevel("admin"));
        try {
            accessLevel("hacker");
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }

    static int accessLevel(String role) {
        switch (role) {
            case "guest": return 0;
            case "user":  return 1;
            case "admin": return 2;
            case "owner": return 3;
            default: throw new IllegalArgumentException("unknown role: " + role);
        }
    }
}
