package com.vbforge.practice.practice_4_access_across_packages_bank;

import com.vbforge.practice.practice_4_access_across_packages_bank.atm.ATM;
import com.vbforge.practice.practice_4_access_across_packages_bank.bank.BankAccount;
import com.vbforge.practice.practice_4_access_across_packages_bank.bank.BankTeller;

/**
 * protected / public across packages: the teller (same package as the account)
 * may deposit, the ATM (other package) may only withdraw.
 */
public class BankAccessDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount(1234, 100.00);

        BankTeller john = new BankTeller();
        john.deposit(account, 100.00);

        ATM atm = new ATM(222);
        atm.withdraw(account, 50.00);

        // account.deposit(10.00);   // compile error here: protected, and this class is in another package
    }
}
