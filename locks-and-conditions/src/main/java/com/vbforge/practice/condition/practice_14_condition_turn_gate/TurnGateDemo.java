package com.vbforge.practice.condition.practice_14_condition_turn_gate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Two threads alternate strictly (ping, pong, ping, ...). One shared condition for both waiters,
 * so passTurn() must use signalAll(): signal() could wake the wrong thread and the turn would stall.
 */
public class TurnGateDemo {

    static class TurnGate {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition turnChanged = lock.newCondition();
        private int turn = 0; // 0 or 1

        void awaitTurn(int me) throws InterruptedException {
            lock.lock();
            try {
                while (turn != me) {
                    turnChanged.await();
                }
            } finally {
                lock.unlock();
            }
        }

        void passTurn(int me) {
            lock.lock();
            try {
                turn = 1 - me;
                turnChanged.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    private static Thread player(TurnGate gate, int id, String word, int rounds) {
        return new Thread(() -> {
            try {
                for (int i = 1; i <= rounds; i++) {
                    gate.awaitTurn(id);
                    System.out.println(word + " " + i);
                    gate.passTurn(id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, word);
    }

    public static void main(String[] args) throws InterruptedException {
        TurnGate gate = new TurnGate();
        Thread pong = player(gate, 1, "pong", 3);
        Thread ping = player(gate, 0, "ping", 3);
        pong.start();
        ping.start();
        ping.join();
        pong.join();
    }
}
