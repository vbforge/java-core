package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AtomicDemo — API contracts")
@Timeout(20)
class AtomicDemoTest {

    @Nested
    @DisplayName("1. AtomicInteger basics")
    class Basics {

        @Test
        @DisplayName("incrementAndGet returns the new value, getAndIncrement the old one")
        void newVersusOld() {
            AtomicInteger value = new AtomicInteger(5);

            assertThat(value.incrementAndGet()).isEqualTo(6);
            assertThat(value.getAndIncrement()).isEqualTo(6);
            assertThat(value.get()).isEqualTo(7);
        }

        @Test
        @DisplayName("addAndGet and getAndSet")
        void addAndSet() {
            AtomicInteger value = new AtomicInteger(7);

            assertThat(value.addAndGet(3)).isEqualTo(10);
            assertThat(value.getAndSet(0)).isEqualTo(10);
            assertThat(value.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("compareAndSet succeeds only when the expected value matches")
        void compareAndSet() {
            AtomicInteger value = new AtomicInteger(0);

            assertThat(value.compareAndSet(0, 42)).isTrue();
            assertThat(value.compareAndSet(0, 99)).isFalse();
            assertThat(value.get()).isEqualTo(42);
        }

        @Test
        @DisplayName("updateAndGet returns the new value, getAndUpdate the old one")
        void functionalUpdates() {
            AtomicInteger value = new AtomicInteger(42);

            assertThat(value.updateAndGet(x -> x * 2)).isEqualTo(84);
            assertThat(value.getAndUpdate(x -> x + 1)).isEqualTo(84);
            assertThat(value.get()).isEqualTo(85);
        }

        @Test
        @DisplayName("accumulateAndGet combines the current value with the argument")
        void accumulate() {
            AtomicInteger value = new AtomicInteger(85);

            assertThat(value.accumulateAndGet(5, Integer::sum)).isEqualTo(90);
        }
    }

    @Nested
    @DisplayName("2. volatile is not enough: the lost update")
    class LostUpdate {

        @Test
        @DisplayName("two volatile increments that overlap produce 1, not 2")
        void volatileLosesAnUpdate() throws InterruptedException {
            AtomicDemo.VolatileCounter counter = new AtomicDemo.VolatileCounter();
            CyclicBarrier bothHaveRead = new CyclicBarrier(2);

            runInParallel(2, i -> {
                int seen = counter.value;
                bothHaveRead.await();
                counter.value = seen + 1;
            });

            assertThat(counter.value).isEqualTo(1);
        }

        @Test
        @DisplayName("incrementAndGet from 4 threads x 10,000 loses nothing")
        void atomicLosesNothing() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();

            runInParallel(4, i -> {
                for (int n = 0; n < 10_000; n++) {
                    counter.incrementAndGet();
                }
            });

            assertThat(counter.get()).isEqualTo(40_000);
        }

        @Test
        @DisplayName("incrementAndGet hands every thread a distinct value")
        void distinctValues() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            List<Integer> seen = Collections.synchronizedList(new ArrayList<Integer>());

            runInParallel(4, i -> {
                for (int n = 0; n < 250; n++) {
                    seen.add(counter.incrementAndGet());
                }
            });

            assertThat(seen).hasSize(1000);
            assertThat(seen).doesNotHaveDuplicates();
        }
    }

    @Nested
    @DisplayName("3. The CAS retry loop")
    class CasLoop {

        @Test
        @DisplayName("atomicMaxWithCasLoop raises the value and returns it")
        void raises() {
            AtomicInteger target = new AtomicInteger(5);

            assertThat(AtomicDemo.atomicMaxWithCasLoop(target, 9)).isEqualTo(9);
            assertThat(target.get()).isEqualTo(9);
        }

        @Test
        @DisplayName("atomicMaxWithCasLoop leaves a larger value alone")
        void doesNotLower() {
            AtomicInteger target = new AtomicInteger(9);

            assertThat(AtomicDemo.atomicMaxWithCasLoop(target, 3)).isEqualTo(9);
            assertThat(target.get()).isEqualTo(9);
        }

        @Test
        @DisplayName("4 threads offering 0..399 leave the maximum, 399")
        void concurrentMax() throws InterruptedException {
            AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);

            runInParallel(4, thread -> {
                for (int i = 0; i < 100; i++) {
                    AtomicDemo.atomicMaxWithCasLoop(max, thread * 100 + i);
                }
            });

            assertThat(max.get()).isEqualTo(399);
        }

        @Test
        @DisplayName("accumulateAndGet with Math::max gives the same result")
        void accumulateMax() throws InterruptedException {
            AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);

            runInParallel(4, thread -> {
                for (int i = 0; i < 100; i++) {
                    max.accumulateAndGet(thread * 100 + i, Math::max);
                }
            });

            assertThat(max.get()).isEqualTo(399);
        }
    }

    @Nested
    @DisplayName("4. AtomicBoolean: do something exactly once")
    class BooleanFlag {

        @Test
        @DisplayName("exactly one of 8 racing threads wins compareAndSet(false, true)")
        void oneWinner() throws InterruptedException {
            AtomicBoolean started = new AtomicBoolean(false);
            AtomicInteger winners = new AtomicInteger();

            runInParallel(8, i -> {
                if (started.compareAndSet(false, true)) {
                    winners.incrementAndGet();
                }
            });

            assertThat(winners.get()).isEqualTo(1);
            assertThat(started.get()).isTrue();
        }

        @Test
        @DisplayName("a second compareAndSet(false, true) fails")
        void secondFails() {
            AtomicBoolean flag = new AtomicBoolean(false);

            assertThat(flag.compareAndSet(false, true)).isTrue();
            assertThat(flag.compareAndSet(false, true)).isFalse();
        }
    }

    @Nested
    @DisplayName("5. AtomicReference compares with ==")
    class Reference {

        @Test
        @DisplayName("an equal but different String object does not match")
        void equalButDifferentObject() {
            String original = new String("state");
            String lookalike = new String("state");
            AtomicReference<String> reference = new AtomicReference<>(original);

            assertThat(original.equals(lookalike)).isTrue();
            assertThat(reference.compareAndSet(lookalike, "next")).isFalse();
            assertThat(reference.get()).isSameAs(original);
        }

        @Test
        @DisplayName("the identical object matches")
        void identicalObject() {
            String original = new String("state");
            AtomicReference<String> reference = new AtomicReference<>(original);

            assertThat(reference.compareAndSet(original, "next")).isTrue();
            assertThat(reference.get()).isEqualTo("next");
        }

        @Test
        @DisplayName("a boxed Integer outside the cache needs the same instance")
        @SuppressWarnings({"deprecation", "removal"})   // new Integer(..) guarantees a distinct object
        void boxedIntegers() {
            Integer big = new Integer(1000);
            AtomicReference<Integer> boxed = new AtomicReference<>(big);

            assertThat(boxed.compareAndSet(new Integer(1000), 2000)).isFalse();
            assertThat(boxed.compareAndSet(big, 2000)).isTrue();
            assertThat(boxed.get()).isEqualTo(2000);
        }

        @Test
        @DisplayName("updateAndGet with an immutable copy loses no update from 4 threads")
        void immutableState() throws InterruptedException {
            AtomicReference<List<String>> snapshot = new AtomicReference<List<String>>(new ArrayList<String>());

            runInParallel(4, thread -> {
                for (int i = 0; i < 100; i++) {
                    final String item = thread + "-" + i;
                    snapshot.updateAndGet(old -> {
                        List<String> next = new ArrayList<>(old);
                        next.add(item);
                        return next;
                    });
                }
            });

            assertThat(snapshot.get()).hasSize(400);
            assertThat(snapshot.get()).doesNotHaveDuplicates();
        }
    }

    @Nested
    @DisplayName("6. The ABA problem")
    class Aba {

        @Test
        @DisplayName("a plain AtomicReference cannot tell A -> B -> A from no change")
        void plainReferenceIsFooled() {
            AtomicReference<String> plain = new AtomicReference<>("A");
            String remembered = plain.get();
            plain.set("B");
            plain.set("A");

            assertThat(plain.compareAndSet(remembered, "C")).isTrue();
        }

        @Test
        @DisplayName("AtomicStampedReference detects A -> B -> A through its stamp")
        void stampedReferenceDetects() {
            AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
            int rememberedStamp = stamped.getStamp();
            stamped.compareAndSet("A", "B", 0, 1);
            stamped.compareAndSet("B", "A", 1, 2);

            assertThat(stamped.compareAndSet("A", "C", rememberedStamp, rememberedStamp + 1)).isFalse();
            assertThat(stamped.getReference()).isEqualTo("A");
            assertThat(stamped.getStamp()).isEqualTo(2);
        }

        @Test
        @DisplayName("with the current stamp the stamped CAS succeeds")
        void stampedReferenceSucceedsWithCurrentStamp() {
            AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 2);

            assertThat(stamped.compareAndSet("A", "C", 2, 3)).isTrue();
            assertThat(stamped.getReference()).isEqualTo("C");
            assertThat(stamped.getStamp()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("7. LongAdder / LongAccumulator")
    class Adder {

        @Test
        @DisplayName("8 threads x 5000 increments sum to 40,000")
        void adderSum() throws InterruptedException {
            LongAdder hits = new LongAdder();

            runInParallel(8, thread -> {
                for (int i = 0; i < 5_000; i++) {
                    hits.increment();
                }
            });

            assertThat(hits.sum()).isEqualTo(40_000L);
        }

        @Test
        @DisplayName("LongAccumulator with Math::max keeps the largest value")
        void accumulatorMax() throws InterruptedException {
            LongAccumulator highest = new LongAccumulator(Math::max, Long.MIN_VALUE);

            runInParallel(8, thread -> {
                for (int i = 0; i < 5_000; i++) {
                    highest.accumulate(thread * 5_000L + i);
                }
            });

            assertThat(highest.get()).isEqualTo(39_999L);
        }

        @Test
        @DisplayName("sumThenReset returns the total and zeroes the adder")
        void sumThenReset() {
            LongAdder adder = new LongAdder();
            adder.add(5);
            adder.add(7);

            assertThat(adder.sumThenReset()).isEqualTo(12L);
            assertThat(adder.sum()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("8. AtomicIntegerArray")
    class ArrayOfAtomics {

        @Test
        @DisplayName("every element is updated atomically")
        void everyElementIsAtomic() throws InterruptedException {
            AtomicIntegerArray buckets = new AtomicIntegerArray(3);

            runInParallel(4, thread -> {
                for (int i = 0; i < 1_000; i++) {
                    for (int bucket = 0; bucket < buckets.length(); bucket++) {
                        buckets.incrementAndGet(bucket);
                    }
                }
            });

            assertThat(buckets.get(0)).isEqualTo(4_000);
            assertThat(buckets.get(1)).isEqualTo(4_000);
            assertThat(buckets.get(2)).isEqualTo(4_000);
        }

        @Test
        @DisplayName("the atomic array copies the source array")
        void copiesTheSource() {
            int[] source = {1, 2, 3};
            AtomicIntegerArray copy = new AtomicIntegerArray(source);

            source[0] = 99;
            copy.set(1, 77);

            assertThat(copy.get(0)).isEqualTo(1);
            assertThat(source[1]).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("9. main() runs every section")
    class MainMethod {

        @Test
        @DisplayName("prints eight sections separated by ---")
        void mainPrintsAllSections() throws InterruptedException {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured, true));
            try {
                AtomicDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            String[] sections = captured.toString().split("(?m)^---$");
            assertThat(sections).hasSize(8);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: start N threads together, wait for all of them
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new AssertionError("a worker thread failed: " + failure.get(), failure.get());
        }
    }
}
