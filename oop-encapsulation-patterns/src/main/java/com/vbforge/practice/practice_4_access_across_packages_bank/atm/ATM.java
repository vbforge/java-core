package com.vbforge.practice.practice_4_access_across_packages_bank.atm;

import com.vbforge.practice.practice_4_access_across_packages_bank.bank.BankAccount;

/** Different package, not a subclass: only the public members of BankAccount are reachable. */
public class ATM {

    private final int id;

    public ATM(int id) {
        this.id = id;
    }

    public void withdraw(BankAccount account, double amount) {
        boolean result = account.withdraw(amount);
        if (result) {
            System.out.println("Successful withdraw on ATM #" + id + " of $" + amount);
        } else {
            System.out.println("Error!");
        }
    }

    // public void deposit(BankAccount account, double amount) {
    //     account.deposit(amount);   // compile error: deposit(double) has protected access in BankAccount
    // }
}
