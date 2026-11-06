package com.vbforge.practice.practice_12_final_fields;

public class FinalFieldWithoutSetterDemo {

    public static void main(String[] args) {
        Account account = new Account("Smith");
        System.out.println("last name: " + account.getLastName());
        // account.setLastName("Brown");   // compile error: there is no setter
    }
}
