package com.vbforge.practice.practice_7_guarded_state_bank_account;

public class GuardedStateBankAccountDemo {

    public static void main(String[] args) {
        System.out.println("=== Guarded state: BankAccount ===\n");

        BankAccount account = new BankAccount("ACC-001", "Alice", 1000.00);
        System.out.println("Created: " + account);

        account.deposit(500.00);
        System.out.println("After deposit  +500: " + account);

        account.withdraw(200.00);
        System.out.println("After withdraw -200: " + account);

        System.out.println("\n-- Validation guards --");
        try {
            account.deposit(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("deposit(-100): " + e.getMessage());
        }

        try {
            account.withdraw(9999);
        } catch (IllegalStateException e) {
            System.out.println("withdraw(9999): " + e.getMessage());
        }

        System.out.println("\nAccount number (no setter): " + account.getAccountNumber());
        // account.setAccountNumber("HACKED");   // compile error: no such method
    }
}
