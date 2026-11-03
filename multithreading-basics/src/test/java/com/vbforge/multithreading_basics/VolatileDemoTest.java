package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VolatileDemo — API contracts")
class VolatileDemoTest {

    private static boolean isVolatile(Class<?> type, String field) throws NoSuchFieldException {
        return Modifier.isVolatile(type.getDeclaredField(field).getModifiers());
    }

    @Nested
    @DisplayName("1. Visibility: volatile stop flag")
    class StopFlag {

        @Test
        @DisplayName("Worker leaves its loop after stop() and reports at least one iteration")
        void workerStops() throws InterruptedException {
            VolatileDemo.StoppableWorker worker = new VolatileDemo.StoppableWorker();
            Thread t = new Thread(worker);
            t.setDaemon(true);
            t.start();
            Thread.sleep(20);
            worker.stop();
            t.join(5_000);
            assertThat(t.isAlive()).isFalse();
            assertThat(worker.getIterations()).isGreaterThan(0);
        }

        @Test
        @DisplayName("The shared flag and the result field are declared volatile")
        void flagsAreVolatile() throws NoSuchFieldException {
            assertThat(isVolatile(VolatileDemo.StoppableWorker.class, "running")).isTrue();
            assertThat(isVolatile(VolatileDemo.StoppableWorker.class, "iterations")).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Safe publication through a volatile flag")
    class Publication {

        @Test
        @DisplayName("Reader that sees the flag also sees the payload written before it")
        void payloadVisible() throws InterruptedException {
            VolatileDemo.Publisher publisher = new VolatileDemo.Publisher();
            AtomicInteger received = new AtomicInteger(-1);
            Thread reader = new Thread(() -> received.set(publisher.awaitPayload()));
            reader.setDaemon(true);
            reader.start();
            Thread.sleep(20);
            publisher.publish(42);
            reader.join(5_000);
            assertThat(reader.isAlive()).isFalse();
            assertThat(received.get()).isEqualTo(42);
        }

        @Test
        @DisplayName("Only the flag is volatile; the payload is a plain field")
        void onlyFlagIsVolatile() throws NoSuchFieldException {
            assertThat(isVolatile(VolatileDemo.Publisher.class, "ready")).isTrue();
            assertThat(isVolatile(VolatileDemo.Publisher.class, "payload")).isFalse();
        }

        @Test
        @DisplayName("awaitPayload() after publish() returns immediately")
        void publishBeforeAwait() {
            VolatileDemo.Publisher publisher = new VolatileDemo.Publisher();
            publisher.publish(7);
            assertThat(publisher.awaitPayload()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("3. volatile is not atomic")
    class NotAtomic {

        @Test
        @DisplayName("volatile counter never exceeds the expected total")
        void neverMoreThanExpected() throws InterruptedException {
            VolatileDemo.VolatileCounter counter = new VolatileDemo.VolatileCounter();
            SynchronizationDemo.hammer(4, 50_000, counter::increment);
            assertThat(counter.get()).isBetween(1, 200_000);
        }

        @Test
        @DisplayName("AtomicInteger gives the exact total")
        void atomicIsExact() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            SynchronizationDemo.hammer(4, 50_000, counter::incrementAndGet);
            assertThat(counter.get()).isEqualTo(200_000);
        }

        @Test
        @DisplayName("The counter field is volatile, yet that does not fix the race")
        void counterIsVolatile() throws NoSuchFieldException {
            assertThat(isVolatile(VolatileDemo.VolatileCounter.class, "count")).isTrue();
        }
    }

    @Nested
    @DisplayName("4. Volatile reference to an immutable snapshot")
    class SnapshotSwap {

        @Test
        @DisplayName("shift() replaces the snapshot with a shifted copy")
        void shiftCreatesNewSnapshot() {
            VolatileDemo.RangeHolder holder = new VolatileDemo.RangeHolder();
            VolatileDemo.Range before = holder.get();
            holder.shift(10);
            VolatileDemo.Range after = holder.get();
            assertThat(after).isNotSameAs(before);
            assertThat(before.getLow()).isEqualTo(0);
            assertThat(after.getLow()).isEqualTo(10);
            assertThat(after.getHigh()).isEqualTo(110);
        }

        @Test
        @DisplayName("Concurrent readers never see a half-updated range")
        void readersSeeConsistentSnapshots() throws InterruptedException {
            VolatileDemo.RangeHolder holder = new VolatileDemo.RangeHolder();
            AtomicBoolean stop = new AtomicBoolean(false);
            AtomicReference<String> violation = new AtomicReference<>();

            Thread reader = new Thread(() -> {
                while (!stop.get()) {
                    VolatileDemo.Range r = holder.get();
                    if (r.getHigh() - r.getLow() != 100) {
                        violation.set("saw [" + r.getLow() + ", " + r.getHigh() + "]");
                        return;
                    }
                }
            });
            reader.setDaemon(true);
            reader.start();
            for (int i = 0; i < 20_000; i++) {
                holder.shift(1);
            }
            stop.set(true);
            reader.join(5_000);
            assertThat(violation.get()).isNull();
            assertThat(holder.get().getLow()).isEqualTo(20_000);
        }

        @Test
        @DisplayName("The holder's reference field is volatile")
        void referenceIsVolatile() throws NoSuchFieldException {
            assertThat(isVolatile(VolatileDemo.RangeHolder.class, "current")).isTrue();
        }
    }
}
