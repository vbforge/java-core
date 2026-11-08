package com.vbforge.practice.liveness.practice_18_lock_ordering;

import com.vbforge.practice.liveness.practice_17_bank_account_fixture.Account;

/**
 * Deadlock PREVENTION: every thread takes the two locks in the same global order (by account id),
 * no matter in which direction the money flows. transfer(a, b) and transfer(b, a) can no longer wait for each other.
 */
public class TransferOrderedDemo {

    static boolean transferOrdered(Account from, Account to, long amount) {
        Account first = from.getId() < to.getId() ? from : to;
        Account second = first == from ? to : from;

        first.getLock().lock();
        try {
            second.getLock().lock();
            try {
                if (from.getBalance() < amount) {
                    return false;
                }
                from.withdraw(amount);
                to.deposit(amount);
                return true;
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1, 1_000);
        Account b = new Account(2, 1_000);
        final int rounds = 1_000;

        Thread aToB = new Thread(() -> {
            for (int i = 0; i < rounds; i++) transferOrdered(a, b, 1);
        }, "a-to-b");
        Thread bToA = new Thread(() -> {
            for (int i = 0; i < rounds; i++) transferOrdered(b, a, 1);
        }, "b-to-a");

        aToB.start();
        bToA.start();
        aToB.join(10_000);
        bToA.join(10_000);

        boolean hung = aToB.isAlive() || bToA.isAlive();
        System.out.println("finished without deadlock: " + !hung);
        System.out.println("balance a=" + a.balanceSnapshot() + ", b=" + b.balanceSnapshot()
                + ", total=" + (a.balanceSnapshot() + b.balanceSnapshot()));
    }
}
