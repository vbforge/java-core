package com.vbforge.practice.practice_18_regex_email_validation;

import java.util.regex.Pattern;

/** Validate an e-mail address with a compiled Pattern; ^ and $ anchor the whole input. */
public class EmailValidationDemo {

    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static void main(String[] args) {
        for (String candidate : new String[] {"example@example.com", "not-an-email"}) { // 2nd sample: addition
            System.out.println(candidate + " -> " + (EMAIL.matcher(candidate).matches() ? "Valid Email" : "Invalid Email"));
        }
    }
}
