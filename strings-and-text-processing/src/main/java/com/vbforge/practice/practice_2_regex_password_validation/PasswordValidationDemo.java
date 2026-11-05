package com.vbforge.practice.practice_2_regex_password_validation;

/**
 * Validate a password with String.matches and regular expressions.
 * One boolean expression: a null guard, a minimum length and four character-class rules
 * (digit, lowercase, uppercase, special). The first failing check short-circuits the rest.
 */
public class PasswordValidationDemo {

    private static final int MIN_LENGTH = 5;

    public static void main(String[] args) {
        System.out.println(isValid("T)123kl@TTO@") + "  - expected true");
        System.out.println(isValid("weakpass")     + "  - expected false");
        System.out.println(isValid("Short1!")      + "  - expected true");
        System.out.println(isValid("NoDigits!")    + "  - expected false");
        System.out.println(isValid("NOLOWER1!")    + "  - expected false");
        System.out.println(isValid("noupper1!")    + "  - expected false");
    }

    public static boolean isValid(String password) {
        return password != null
                && password.length() >= MIN_LENGTH
                && password.matches(".*[0-9].*")               // at least one digit
                && password.matches(".*[a-z].*")               // at least one lowercase
                && password.matches(".*[A-Z].*")               // at least one uppercase
                && password.matches(".*[^a-zA-Z0-9].*");       // at least one special
    }
}
