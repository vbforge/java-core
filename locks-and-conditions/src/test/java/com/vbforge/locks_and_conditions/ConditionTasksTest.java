package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConditionTasks — your implementations")
class ConditionTasksTest {

    private ConditionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ConditionTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement Mailbox (put / take)")
    @DisplayName("Mailbox")
    class MailboxTest {

        @Test
        @DisplayName("put then take returns the item")
        void javadocExample() throws InterruptedException {
            ConditionTasks.Mailbox<String> box = new ConditionTasks.Mailbox<>();
            box.put("a");
            assertThat(box.take()).isEqualTo("a");
        }

        @Test
        @DisplayName("take blocks on an empty mailbox until put is called")
        void takeBlocks() throws InterruptedException {
            ConditionTasks.Mailbox<Integer> box = new ConditionTasks.Mailbox<>();
            AtomicInteger got = new AtomicInteger(-1);
            Thread consumer = new Thread(() -> {
                try {
                    got.set(box.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            while (consumer.getState() != Thread.State.WAITING) {
                Thread.yield();
            }
            assertThat(got.get()).isEqualTo(-1);

            box.put(7);
            consumer.join();

            assertThat(got.get()).isEqualTo(7);
        }

        @Test
        @DisplayName("put blocks on a full mailbox until take is called")
        void putBlocks() throws InterruptedException {
            ConditionTasks.Mailbox<String> box = new ConditionTasks.Mailbox<>();
            box.put("a");
            Thread producer = new Thread(() -> {
                try {
                    box.put("b");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producer.start();
            while (producer.getState() != Thread.State.WAITING) {
                Thread.yield();
            }

            assertThat(box.take()).isEqualTo("a");
            producer.join();

            assertThat(box.take()).isEqualTo("b");
        }

        @Test
        @DisplayName("100 items pass through in order")
        void ordered() throws InterruptedException {
            ConditionTasks.Mailbox<Integer> box = new ConditionTasks.Mailbox<>();
            List<Integer> received = Collections.synchronizedList(new ArrayList<>());
            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        received.add(box.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            for (int i = 0; i < 100; i++) {
                box.put(i);
            }
            consumer.join();

            assertThat(received).hasSize(100).isSorted();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement awaitFlag()")
    @DisplayName("awaitFlag")
    class AwaitFlagTest {

        @Test
        @DisplayName("Flag already true: returns true at once")
        void alreadyTrue() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            Condition cond = lock.newCondition();
            lock.lock();
            try {
                assertThat(tasks.awaitFlag(lock, cond, new AtomicBoolean(true), 5_000)).isTrue();
                assertThat(lock.isHeldByCurrentThread()).isTrue();
            } finally {
                lock.unlock();
            }
        }

        @Test
        @DisplayName("Flag never set: returns false after the timeout")
        void timesOut() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            Condition cond = lock.newCondition();
            lock.lock();
            try {
                long start = System.nanoTime();
                boolean result = tasks.awaitFlag(lock, cond, new AtomicBoolean(false), 30);
                long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

                assertThat(result).isFalse();
                assertThat(elapsedMillis).isGreaterThanOrEqualTo(25);
                assertThat(lock.isHeldByCurrentThread()).isTrue();
            } finally {
                lock.unlock();
            }
        }

        @Test
        @DisplayName("Flag set and signalled by another thread: returns true")
        void signalled() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            Condition cond = lock.newCondition();
            AtomicBoolean ready = new AtomicBoolean(false);
            Thread setter = new Thread(() -> {
                lock.lock();
                try {
                    ready.set(true);
                    cond.signalAll();
                } finally {
                    lock.unlock();
                }
            });
            lock.lock();
            try {
                setter.start();
                assertThat(tasks.awaitFlag(lock, cond, ready, 10_000)).isTrue();
            } finally {
                lock.unlock();
            }
            setter.join();
        }

        @Test
        @DisplayName("Spurious signal with the flag still false keeps waiting until the timeout")
        void ignoresSignalWithoutFlag() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            Condition cond = lock.newCondition();
            AtomicBoolean ready = new AtomicBoolean(false);
            Thread noisy = new Thread(() -> {
                lock.lock();
                try {
                    cond.signalAll();                // flag is still false
                } finally {
                    lock.unlock();
                }
            });
            lock.lock();
            try {
                noisy.start();
                assertThat(tasks.awaitFlag(lock, cond, ready, 50)).isFalse();
            } finally {
                lock.unlock();
            }
            noisy.join();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement TurnGate (awaitTurn / passTurn)")
    @DisplayName("TurnGate")
    class TurnGateTest {

        @Test
        @DisplayName("A may go first without waiting")
        void aStarts() throws InterruptedException {
            ConditionTasks.TurnGate gate = new ConditionTasks.TurnGate();
            gate.awaitTurn(true);
        }

        @Test
        @DisplayName("B blocks until A passes the turn")
        void bWaitsForA() throws InterruptedException {
            ConditionTasks.TurnGate gate = new ConditionTasks.TurnGate();
            AtomicBoolean bRan = new AtomicBoolean();
            Thread b = new Thread(() -> {
                try {
                    gate.awaitTurn(false);
                    bRan.set(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            b.start();
            while (b.getState() != Thread.State.WAITING) {
                Thread.yield();
            }
            assertThat(bRan).isFalse();

            gate.awaitTurn(true);
            gate.passTurn();
            b.join();

            assertThat(bRan).isTrue();
        }

        @Test
        @DisplayName("Two threads alternate: A B A B A B A B A B")
        void alternating() throws InterruptedException {
            ConditionTasks.TurnGate gate = new ConditionTasks.TurnGate();
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            Thread a = new Thread(() -> loop(gate, true, "A", log));
            Thread b = new Thread(() -> loop(gate, false, "B", log));
            b.start();
            a.start();
            a.join();
            b.join();

            assertThat(log).containsExactly("A", "B", "A", "B", "A", "B", "A", "B", "A", "B");
        }

        private void loop(ConditionTasks.TurnGate gate, boolean sideA, String name, List<String> log) {
            try {
                for (int i = 0; i < 5; i++) {
                    gate.awaitTurn(sideA);
                    log.add(name);
                    gate.passTurn();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
