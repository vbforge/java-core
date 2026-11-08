package com.vbforge.practice.liveness.practice_17_bank_account_fixture;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Shared fixture, not a concept: an account with a unique immutable id (used as the global lock order),
 * its own ReentrantLock and a balance guarded by that lock. Used by practice_18 and practice_19.
 * The balance methods expect the CALLER to hold the lock.
 */
public class Account {

    private final long id;
    private final ReentrantLock lock = new ReentrantLock();
    private long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    /** Caller must hold the lock. */
    public long getBalance() {
        return balance;
    }

    /** Caller must hold the lock. */
    public void withdraw(long amount) {
        balance -= amount;
    }

    /** Caller must hold the lock. */
    public void deposit(long amount) {
        balance += amount;
    }

    /** Locked read, for printing results. */
    public long balanceSnapshot() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
