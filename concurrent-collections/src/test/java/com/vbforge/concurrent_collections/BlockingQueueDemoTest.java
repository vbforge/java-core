package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BlockingQueueDemo — API contracts")
@Timeout(20)
class BlockingQueueDemoTest {

    @Nested
    @DisplayName("1. Four flavours of every operation")
    class OperationFlavours {

        @Test
        @DisplayName("poll and peek on an empty queue return null")
        void specialValueOnEmpty() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

            assertThat(queue.poll()).isNull();
            assertThat(queue.peek()).isNull();
        }

        @Test
        @DisplayName("timed poll on an empty queue returns null after waiting")
        void timedPollOnEmpty() throws InterruptedException {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

            assertThat(queue.poll(10, TimeUnit.MILLISECONDS)).isNull();
        }

        @Test
        @DisplayName("remove and element on an empty queue throw NoSuchElementException")
        void exceptionOnEmpty() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

            assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("offer on a full queue returns false")
        void offerOnFull() throws InterruptedException {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            queue.add("first");

            assertThat(queue.offer("second")).isFalse();
            assertThat(queue.offer("second", 10, TimeUnit.MILLISECONDS)).isFalse();
            assertThat(queue).containsExactly("first");
        }

        @Test
        @DisplayName("add on a full queue throws IllegalStateException instead of waiting")
        void addOnFull() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            queue.add("first");

            assertThatThrownBy(() -> queue.add("second")).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("peek does not remove the element")
        void peekKeeps() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            queue.add("first");

            assertThat(queue.peek()).isEqualTo("first");
            assertThat(queue.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("2. Capacity and nulls")
    class CapacityAndNulls {

        @Test
        @DisplayName("ArrayBlockingQueue is bounded by its constructor argument")
        void arrayBlockingQueueCapacity() {
            assertThat(new ArrayBlockingQueue<String>(3).remainingCapacity()).isEqualTo(3);
        }

        @Test
        @DisplayName("LinkedBlockingQueue without an argument is effectively unbounded")
        void linkedBlockingQueueDefault() {
            assertThat(new LinkedBlockingQueue<String>().remainingCapacity()).isEqualTo(Integer.MAX_VALUE);
            assertThat(new LinkedBlockingQueue<String>(2).remainingCapacity()).isEqualTo(2);
        }

        @Test
        @DisplayName("PriorityBlockingQueue is unbounded and SynchronousQueue has capacity zero")
        void otherCapacities() {
            assertThat(new PriorityBlockingQueue<Integer>().remainingCapacity()).isEqualTo(Integer.MAX_VALUE);
            assertThat(new SynchronousQueue<String>().remainingCapacity()).isEqualTo(0);
        }

        @Test
        @DisplayName("remainingCapacity shrinks as the queue fills")
        void remainingCapacityShrinks() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
            queue.add("a");

            assertThat(queue.remainingCapacity()).isEqualTo(2);
        }

        @Test
        @DisplayName("null elements are rejected")
        void nullsRejected() {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();

            assertThatThrownBy(() -> queue.put(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> queue.offer(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> queue.add(null)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("3. Producer / consumer with a poison pill")
    class ProducerConsumer {

        @Test
        @DisplayName("the consumer receives everything the producer put, in order, then stops")
        void producerConsumer() throws InterruptedException {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 10; i++) {
                        queue.put(i);
                    }
                    queue.put(-1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producer.start();

            List<Integer> received = new ArrayList<>();
            while (true) {
                int item = queue.take();
                if (item == -1) {
                    break;
                }
                received.add(item);
            }
            producer.join();

            assertThat(received).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("take blocks until an element arrives")
        void takeBlocks() throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            AtomicReference<String> received = new AtomicReference<>();
            Thread consumer = new Thread(() -> {
                try {
                    received.set(queue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            awaitState(consumer, Thread.State.WAITING);
            assertThat(received.get()).isNull();

            queue.put("hello");
            consumer.join();

            assertThat(received.get()).isEqualTo("hello");
        }
    }

    @Nested
    @DisplayName("4. Back-pressure")
    class BackPressure {

        @Test
        @DisplayName("a producer blocks in put when the queue is full")
        void producerBlocks() throws InterruptedException {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 5; i++) {
                        queue.put(i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producer.start();

            awaitState(producer, Thread.State.WAITING);
            assertThat(queue.size()).isEqualTo(2);
            assertThat(queue.remainingCapacity()).isEqualTo(0);

            List<Integer> received = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                received.add(queue.take());
            }
            producer.join();

            assertThat(received).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("a timed offer gives up on a full queue instead of blocking forever")
        void timedOfferGivesUp() throws InterruptedException {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
            queue.put(1);

            assertThat(queue.offer(2, 20, TimeUnit.MILLISECONDS)).isFalse();
        }
    }

    @Nested
    @DisplayName("5. Several consumers, one pill each")
    class MultipleConsumers {

        @Test
        @DisplayName("every item is processed exactly once and every consumer stops")
        void eachItemProcessedOnce() throws InterruptedException {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            BlockingQueue<Integer> processed = new LinkedBlockingQueue<>();
            int consumerCount = 3;
            Thread[] consumers = new Thread[consumerCount];
            for (int c = 0; c < consumerCount; c++) {
                consumers[c] = new Thread(() -> {
                    try {
                        int item;
                        while ((item = queue.take()) != -1) {
                            processed.add(item);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                consumers[c].start();
            }

            for (int i = 1; i <= 100; i++) {
                queue.put(i);
            }
            for (int c = 0; c < consumerCount; c++) {
                queue.put(-1);
            }
            for (Thread consumer : consumers) {
                consumer.join();
            }

            assertThat(processed).hasSize(100);
            assertThat(new HashSet<>(processed)).hasSize(100);
            assertThat(queue).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. drainTo")
    class DrainTo {

        @Test
        @DisplayName("drainTo with a maximum moves at most that many, in order")
        void drainWithMax() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            for (int i = 1; i <= 5; i++) {
                queue.add(i);
            }
            List<Integer> batch = new ArrayList<>();

            assertThat(queue.drainTo(batch, 3)).isEqualTo(3);
            assertThat(batch).containsExactly(1, 2, 3);
            assertThat(queue).containsExactly(4, 5);
        }

        @Test
        @DisplayName("drainTo without a maximum moves everything")
        void drainAll() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            queue.add(1);
            queue.add(2);
            List<Integer> batch = new ArrayList<>();

            assertThat(queue.drainTo(batch)).isEqualTo(2);
            assertThat(batch).containsExactly(1, 2);
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("drainTo on an empty queue returns 0 immediately")
        void drainEmpty() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

            assertThat(queue.drainTo(new ArrayList<Integer>())).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("7. PriorityBlockingQueue")
    class Priority {

        @Test
        @DisplayName("take returns the smallest element first by natural order")
        void naturalOrder() throws InterruptedException {
            PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
            queue.add(5);
            queue.add(1);
            queue.add(3);

            assertThat(queue.take()).isEqualTo(1);
            assertThat(queue.take()).isEqualTo(3);
            assertThat(queue.take()).isEqualTo(5);
        }

        @Test
        @DisplayName("a Comparator changes the order")
        void customComparator() throws InterruptedException {
            PriorityBlockingQueue<Integer> queue =
                    new PriorityBlockingQueue<>(11, Collections.<Integer>reverseOrder());
            queue.add(5);
            queue.add(1);
            queue.add(3);

            assertThat(queue.take()).isEqualTo(5);
            assertThat(queue.take()).isEqualTo(3);
            assertThat(queue.take()).isEqualTo(1);
        }

        @Test
        @DisplayName("put never blocks because the queue is unbounded")
        void neverBlocks() throws InterruptedException {
            PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
            for (int i = 0; i < 10_000; i++) {
                queue.put(i);
            }

            assertThat(queue.size()).isEqualTo(10_000);
        }
    }

    @Nested
    @DisplayName("8. SynchronousQueue")
    class Synchronous {

        @Test
        @DisplayName("offer without a waiting consumer returns false and stores nothing")
        void offerWithoutConsumer() {
            SynchronousQueue<String> queue = new SynchronousQueue<>();

            assertThat(queue.offer("lost")).isFalse();
            assertThat(queue.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("put hands the element straight to a waiting consumer")
        void handOff() throws InterruptedException {
            SynchronousQueue<String> queue = new SynchronousQueue<>();
            AtomicReference<String> received = new AtomicReference<>();
            Thread consumer = new Thread(() -> {
                try {
                    received.set(queue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();

            queue.put("hello");
            consumer.join();

            assertThat(received.get()).isEqualTo("hello");
        }
    }

    @Nested
    @DisplayName("9. DelayQueue")
    class Delay {

        @Test
        @DisplayName("poll returns null while no element has expired")
        void pollBeforeExpiry() {
            DelayQueue<BlockingQueueDemo.DelayedMessage> queue = new DelayQueue<>();
            queue.put(new BlockingQueueDemo.DelayedMessage("late", 60_000));

            assertThat(queue.poll()).isNull();
            assertThat(queue.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("take returns elements in the order in which their delays expire")
        void expiryOrder() throws InterruptedException {
            DelayQueue<BlockingQueueDemo.DelayedMessage> queue = new DelayQueue<>();
            queue.put(new BlockingQueueDemo.DelayedMessage("late", 100));
            queue.put(new BlockingQueueDemo.DelayedMessage("early", 50));

            assertThat(queue.take().text()).isEqualTo("early");
            assertThat(queue.take().text()).isEqualTo("late");
        }

        @Test
        @DisplayName("take waits at least as long as the delay")
        void takeWaits() throws InterruptedException {
            DelayQueue<BlockingQueueDemo.DelayedMessage> queue = new DelayQueue<>();
            long start = System.nanoTime();
            queue.put(new BlockingQueueDemo.DelayedMessage("m", 50));

            queue.take();

            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            assertThat(elapsedMillis).isGreaterThanOrEqualTo(45);
        }
    }

    @Nested
    @DisplayName("10. Interruption")
    class Interruption {

        @Test
        @DisplayName("interrupting a thread blocked in take throws InterruptedException and clears the flag")
        void interruptWakesTake() throws InterruptedException {
            BlockingQueue<String> emptyQueue = new ArrayBlockingQueue<>(1);
            AtomicBoolean caught = new AtomicBoolean();
            AtomicBoolean flagAfterCatch = new AtomicBoolean(true);
            AtomicBoolean flagAfterRestore = new AtomicBoolean();
            Thread worker = new Thread(() -> {
                try {
                    emptyQueue.take();
                } catch (InterruptedException e) {
                    caught.set(true);
                    flagAfterCatch.set(Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    flagAfterRestore.set(Thread.currentThread().isInterrupted());
                }
            });
            worker.start();
            awaitState(worker, Thread.State.WAITING);

            worker.interrupt();
            worker.join();

            assertThat(caught.get()).isTrue();
            assertThat(flagAfterCatch.get()).isFalse();
            assertThat(flagAfterRestore.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("11. main() runs every section")
    class MainMethod {

        @Test
        @DisplayName("prints ten sections separated by ---")
        void mainPrintsAllSections() throws InterruptedException {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured, true));
            try {
                BlockingQueueDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            String[] sections = captured.toString().split("(?m)^---$");
            assertThat(sections).hasSize(10);
        }
    }

    private static void awaitState(Thread thread, Thread.State expected) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (thread.getState() != expected) {
            if (System.nanoTime() > deadline) {
                throw new AssertionError(thread.getName() + " never reached " + expected);
            }
            Thread.sleep(1);
        }
    }
}
