package com.vbforge.practice.practice_4_exception_testing;

public class BankAccountDemo {
    public static void main(String[] args) {
        BankAccount account = new BankAccount(1_000);
        account.withdraw(300);
        System.out.println("balance = " + account.balance());
        try {
            account.withdraw(5_000);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }
}
