package com.vbforge.practice.liveness.practice_19_try_lock_back_off;

import com.vbforge.practice.liveness.practice_17_bank_account_fixture.Account;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Deadlock AVOIDANCE: take locks with tryLock(timeout). If the second lock does not come in time,
 * release the FIRST one as well (clean release), report failure and let the caller retry later.
 * No thread ever waits forever while holding a lock.
 */
public class TransferWithTimeoutDemo {

    static boolean transferWithTimeout(Account from, Account to, long amount, long timeoutMillis)
            throws InterruptedException {
        if (!from.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
            return false;
        }
        try {
            if (!to.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                return false; // finally below releases 'from'
            }
            try {
                if (from.getBalance() < amount) {
                    return false;
                }
                from.withdraw(amount);
                to.deposit(amount);
                return true;
            } finally {
                to.getLock().unlock();
            }
        } finally {
            from.getLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account from = new Account(1, 100);
        Account to = new Account(2, 0);

        // another thread keeps the destination account locked for a while
        CountDownLatch holding = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread blocker = new Thread(() -> {
            to.getLock().lock();
            try {
                holding.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                to.getLock().unlock();
            }
        }, "blocker");
        blocker.start();
        holding.await();

        boolean busy = transferWithTimeout(from, to, 30, 50);
        System.out.println("destination busy, transfer ok: " + busy);
        System.out.println("source lock released after failure: " + !from.getLock().isLocked());

        release.countDown();
        blocker.join();

        boolean free = transferWithTimeout(from, to, 30, 50);
        System.out.println("destination free, transfer ok: " + free);
        System.out.println("balances: from=" + from.balanceSnapshot() + ", to=" + to.balanceSnapshot());
    }
}
