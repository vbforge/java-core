package com.vbforge.virtual_threads_intro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VirtualThreadPitfallsDemo — API contracts")
class VirtualThreadPitfallsDemoTest {

    @Nested
    @DisplayName("1. Limit concurrency with a Semaphore")
    class SemaphoreLimit {

        @Test
        @DisplayName("tryAcquire succeeds only while permits remain")
        void permitsRunOut() {
            Semaphore semaphore = new Semaphore(2);

            assertThat(semaphore.tryAcquire()).isTrue();
            assertThat(semaphore.tryAcquire()).isTrue();
            assertThat(semaphore.tryAcquire()).isFalse();
            assertThat(semaphore.availablePermits()).isZero();
        }

        @Test
        @DisplayName("release gives a permit back")
        void releaseGivesPermitBack() {
            Semaphore semaphore = new Semaphore(1);
            semaphore.acquireUninterruptibly();

            semaphore.release();

            assertThat(semaphore.availablePermits()).isEqualTo(1);
        }

        @Test
        @DisplayName("a virtual thread without a permit parks in WAITING until one is released")
        void virtualThreadWaitsForPermit() throws Exception {
            Semaphore semaphore = new Semaphore(0);
            Thread waiter = Thread.ofVirtual().start(semaphore::acquireUninterruptibly);

            long deadline = System.nanoTime() + 10_000_000_000L;
            while (waiter.getState() != Thread.State.WAITING && System.nanoTime() < deadline) {
                Thread.sleep(1);
            }
            assertThat(waiter.getState()).isEqualTo(Thread.State.WAITING);

            semaphore.release();
            waiter.join();

            assertThat(waiter.getState()).isEqualTo(Thread.State.TERMINATED);
        }
    }

    @Nested
    @DisplayName("2. ThreadLocal")
    class ThreadLocals {

        private final ThreadLocal<String> user = ThreadLocal.withInitial(() -> "anonymous");

        @Test
        @DisplayName("a new virtual thread does not see the parent's value")
        void notInherited() throws Exception {
            user.set("main-user");
            AtomicReference<String> seen = new AtomicReference<>();

            Thread.startVirtualThread(() -> seen.set(user.get())).join();

            assertThat(seen.get()).isEqualTo("anonymous");
            user.remove();
        }

        @Test
        @DisplayName("a value set in a virtual thread does not leak back to the parent")
        void notLeakedBack() throws Exception {
            user.set("main-user");

            Thread.startVirtualThread(() -> user.set("virtual-user")).join();

            assertThat(user.get()).isEqualTo("main-user");
            user.remove();
        }

        @Test
        @DisplayName("two virtual threads each keep their own copy")
        void eachThreadHasOwnCopy() throws Exception {
            AtomicReference<String> first = new AtomicReference<>();
            AtomicReference<String> second = new AtomicReference<>();

            Thread a = Thread.startVirtualThread(() -> {
                user.set("a");
                first.set(user.get());
            });
            Thread b = Thread.startVirtualThread(() -> {
                user.set("b");
                second.set(user.get());
            });
            a.join();
            b.join();

            assertThat(first.get()).isEqualTo("a");
            assertThat(second.get()).isEqualTo("b");
        }
    }

    @Nested
    @DisplayName("3. synchronized vs ReentrantLock")
    class Locking {

        @Test
        @DisplayName("synchronized counter is exact: threads × increments")
        void synchronizedIsCorrect() throws Exception {
            assertThat(VirtualThreadPitfallsDemo.countWithSynchronized(100, 1_000)).isEqualTo(100_000);
        }

        @Test
        @DisplayName("ReentrantLock counter is exact: threads × increments")
        void reentrantLockIsCorrect() throws Exception {
            assertThat(VirtualThreadPitfallsDemo.countWithReentrantLock(100, 1_000)).isEqualTo(100_000);
        }

        @Test
        @DisplayName("zero threads → counter stays 0 for both variants")
        void zeroThreads() throws Exception {
            assertThat(VirtualThreadPitfallsDemo.countWithSynchronized(0, 1_000)).isZero();
            assertThat(VirtualThreadPitfallsDemo.countWithReentrantLock(0, 1_000)).isZero();
        }

        @Test
        @DisplayName("ReentrantLock is reentrant: hold count grows and falls with each lock/unlock")
        void lockIsReentrant() {
            ReentrantLock lock = new ReentrantLock();

            lock.lock();
            lock.lock();
            assertThat(lock.getHoldCount()).isEqualTo(2);

            lock.unlock();
            lock.unlock();
            assertThat(lock.isLocked()).isFalse();
        }
    }
}
