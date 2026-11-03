package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ConditionDemo — API contracts")
class ConditionDemoTest {

    @Nested
    @DisplayName("1. Bounded buffer, two conditions")
    class BoundedBufferTest {

        @Test
        @DisplayName("put then take returns items in FIFO order")
        void fifo() throws InterruptedException {
            ConditionDemo.BoundedBuffer<String> buffer = new ConditionDemo.BoundedBuffer<>(3);
            buffer.put("a");
            buffer.put("b");
            assertThat(buffer.take()).isEqualTo("a");
            assertThat(buffer.take()).isEqualTo("b");
        }

        @Test
        @DisplayName("offer returns false when the buffer is full")
        void offerWhenFull() throws InterruptedException {
            ConditionDemo.BoundedBuffer<Integer> buffer = new ConditionDemo.BoundedBuffer<>(1);
            buffer.put(1);
            assertThat(buffer.offer(2)).isFalse();
            assertThat(buffer.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Non-positive capacity is rejected")
        void badCapacity() {
            assertThatThrownBy(() -> new ConditionDemo.BoundedBuffer<String>(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("take blocks until a producer puts an item")
        void takeBlocksUntilPut() throws InterruptedException {
            ConditionDemo.BoundedBuffer<Integer> buffer = new ConditionDemo.BoundedBuffer<>(1);
            AtomicInteger got = new AtomicInteger(-1);
            Thread consumer = new Thread(() -> {
                try {
                    got.set(buffer.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            while (consumer.getState() != Thread.State.WAITING) {
                Thread.yield();
            }
            assertThat(got.get()).isEqualTo(-1);
            buffer.put(42);
            consumer.join();
            assertThat(got.get()).isEqualTo(42);
        }

        @Test
        @DisplayName("put blocks while full and resumes after a take")
        void putBlocksWhenFull() throws InterruptedException {
            ConditionDemo.BoundedBuffer<Integer> buffer = new ConditionDemo.BoundedBuffer<>(1);
            buffer.put(1);
            Thread producer = new Thread(() -> {
                try {
                    buffer.put(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producer.start();
            while (producer.getState() != Thread.State.WAITING) {
                Thread.yield();
            }
            assertThat(buffer.take()).isEqualTo(1);
            producer.join();
            assertThat(buffer.take()).isEqualTo(2);
        }

        @Test
        @DisplayName("Producer and consumer exchange 1..10 through a 2-slot buffer")
        void producerConsumer() throws InterruptedException {
            ConditionDemo.BoundedBuffer<Integer> buffer = new ConditionDemo.BoundedBuffer<>(2);
            AtomicInteger sum = new AtomicInteger();
            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        sum.addAndGet(buffer.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            for (int i = 1; i <= 10; i++) {
                buffer.put(i);
            }
            consumer.join();
            assertThat(sum.get()).isEqualTo(55);
        }
    }

    @Nested
    @DisplayName("2. await(timeout)")
    class AwaitTimeout {

        @Test
        @DisplayName("Unsignalled wait returns false after the timeout")
        void timesOut() throws InterruptedException {
            assertThat(ConditionDemo.awaitWithTimeout(20)).isFalse();
        }

        @Test
        @DisplayName("A signal before the timeout makes await return true")
        void signalled() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            Condition cond = lock.newCondition();
            boolean[] result = new boolean[1];
            Thread waiter = new Thread(() -> {
                lock.lock();
                try {
                    result[0] = cond.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            });
            waiter.start();
            lock.lock();
            try {
                while (lock.getWaitQueueLength(cond) == 0) {
                    lock.unlock();
                    Thread.yield();
                    lock.lock();
                }
                cond.signal();
            } finally {
                lock.unlock();
            }
            waiter.join();
            assertThat(result[0]).isTrue();
        }
    }

    @Nested
    @DisplayName("3. signal vs signalAll")
    class SignalAll {

        @Test
        @DisplayName("signalAll releases every waiting thread")
        void signalAllReleasesAll() throws InterruptedException {
            ConditionDemo.Gate gate = new ConditionDemo.Gate();
            Thread[] threads = new Thread[3];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        gate.awaitOpen();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                threads[i].start();
            }
            while (gate.waiting() < 3) {
                Thread.yield();
            }
            gate.openForAll();
            for (Thread t : threads) {
                t.join();
            }
            assertThat(gate.passed()).isEqualTo(3);
        }

        @Test
        @DisplayName("signal() alone does not let a thread pass while the predicate is false")
        void signalNeedsPredicate() throws InterruptedException {
            ConditionDemo.Gate gate = new ConditionDemo.Gate();
            Thread waiter = new Thread(() -> {
                try {
                    gate.awaitOpen();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            waiter.start();
            while (gate.waiting() < 1) {
                Thread.yield();
            }
            gate.openForOne();                       // wakes it, but 'open' is still false
            while (gate.waiting() < 1) {
                Thread.yield();                      // it goes back to waiting (while loop)
            }
            assertThat(gate.passed()).isZero();
            gate.openForAll();
            waiter.join();
            assertThat(gate.passed()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("4. Lock must be held")
    class LockMustBeHeld {

        @Test
        @DisplayName("signal() without the lock throws")
        void signalWithoutLock() {
            assertThat(ConditionDemo.signalWithoutLockRejected()).isTrue();
        }

        @Test
        @DisplayName("await() without the lock throws")
        void awaitWithoutLock() {
            Condition cond = new ReentrantLock().newCondition();
            assertThatThrownBy(cond::await).isInstanceOf(IllegalMonitorStateException.class);
        }

        @Test
        @DisplayName("A condition of another lock cannot be signalled")
        void foreignCondition() {
            ReentrantLock mine = new ReentrantLock();
            Condition foreign = new ReentrantLock().newCondition();
            mine.lock();
            try {
                assertThatThrownBy(foreign::signal).isInstanceOf(IllegalMonitorStateException.class);
            } finally {
                mine.unlock();
            }
        }
    }
}
