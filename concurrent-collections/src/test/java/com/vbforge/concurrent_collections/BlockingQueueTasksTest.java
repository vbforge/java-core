package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlockingQueueTasks — your implementations")
class BlockingQueueTasksTest {

    private BlockingQueueTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BlockingQueueTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumWithProducerConsumer()")
    @DisplayName("sumWithProducerConsumer")
    @Timeout(10)
    class SumWithProducerConsumerTest {

        @Test
        @DisplayName("sums 1..5 through a queue of capacity 2 (Javadoc example)")
        void example() throws InterruptedException {
            assertThat(tasks.sumWithProducerConsumer(Arrays.asList(1, 2, 3, 4, 5), 2)).isEqualTo(15L);
        }

        @Test
        @DisplayName("an empty list sums to 0 and does not hang")
        void emptyInput() throws InterruptedException {
            assertThat(tasks.sumWithProducerConsumer(Collections.<Integer>emptyList(), 3)).isEqualTo(0L);
        }

        @Test
        @DisplayName("a single element through a queue of capacity 1")
        void singleElement() throws InterruptedException {
            assertThat(tasks.sumWithProducerConsumer(Collections.singletonList(7), 1)).isEqualTo(7L);
        }

        @Test
        @DisplayName("zero is real data, not a stop signal")
        void zerosAreData() throws InterruptedException {
            assertThat(tasks.sumWithProducerConsumer(Arrays.asList(0, 5, 0, 5), 2)).isEqualTo(10L);
        }

        @Test
        @DisplayName("the sum may exceed Integer.MAX_VALUE")
        void largeSum() throws InterruptedException {
            List<Integer> big = Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE, 2);

            assertThat(tasks.sumWithProducerConsumer(big, 1)).isEqualTo(2L * Integer.MAX_VALUE + 2);
        }

        @ParameterizedTest(name = "capacity {0} gives the same sum")
        @CsvSource({"1", "2", "7", "1000"})
        void anyCapacity(int capacity) throws InterruptedException {
            List<Integer> numbers = new ArrayList<>();
            for (int i = 1; i <= 500; i++) {
                numbers.add(i);
            }

            assertThat(tasks.sumWithProducerConsumer(numbers, capacity)).isEqualTo(125_250L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement drainBatch()")
    @DisplayName("drainBatch")
    @Timeout(10)
    class DrainBatchTest {

        @Test
        @DisplayName("drains in batches, then returns an empty list at once (Javadoc example)")
        void example() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(Arrays.asList(1, 2, 3, 4, 5));

            assertThat(tasks.drainBatch(queue, 3)).containsExactly(1, 2, 3);
            assertThat(queue).containsExactly(4, 5);

            assertThat(tasks.drainBatch(queue, 10)).containsExactly(4, 5);
            assertThat(queue).isEmpty();

            assertThat(tasks.drainBatch(queue, 10)).isEmpty();
        }

        @Test
        @DisplayName("max 0 takes nothing")
        void maxZero() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(Arrays.asList(1, 2));

            assertThat(tasks.drainBatch(queue, 0)).isEmpty();
            assertThat(queue).containsExactly(1, 2);
        }

        @Test
        @DisplayName("max larger than the queue takes everything")
        void maxLargerThanQueue() {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
            queue.add(9);

            assertThat(tasks.drainBatch(queue, 100)).containsExactly(9);
        }

        @Test
        @DisplayName("an empty queue returns an empty list without waiting")
        void emptyQueue() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            long start = System.nanoTime();

            assertThat(tasks.drainBatch(queue, 5)).isEmpty();

            assertThat(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)).isLessThan(500);
        }

        @Test
        @DisplayName("returns a new list that does not alias the queue")
        void returnsNewList() {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(Arrays.asList(1));
            List<Integer> batch = tasks.drainBatch(queue, 1);
            queue.add(2);

            assertThat(batch).containsExactly(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pollWithTimeout()")
    @DisplayName("pollWithTimeout")
    @Timeout(10)
    class PollWithTimeoutTest {

        @Test
        @DisplayName("returns an available element at once (Javadoc example)")
        void example() throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>(Collections.singletonList("a"));

            assertThat(tasks.pollWithTimeout(queue, 100)).isEqualTo(Optional.of("a"));
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("an empty queue returns Optional.empty after about the timeout")
        void timesOut() throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            long start = System.nanoTime();

            Optional<String> result = tasks.pollWithTimeout(queue, 50);

            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            assertThat(result).isEmpty();
            assertThat(elapsed).isGreaterThanOrEqualTo(45);
        }

        @Test
        @DisplayName("returns as soon as an element arrives, not at the deadline")
        void returnsWhenElementArrives() throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            Thread late = new Thread(() -> {
                try {
                    Thread.sleep(30);
                    queue.put("b");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            late.start();
            long start = System.nanoTime();

            Optional<String> result = tasks.pollWithTimeout(queue, 5000);

            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            late.join();
            assertThat(result).isEqualTo(Optional.of("b"));
            assertThat(elapsed).isLessThan(3000);
        }

        @Test
        @DisplayName("a zero timeout on an empty queue returns empty immediately")
        void zeroTimeout() throws InterruptedException {
            assertThat(tasks.pollWithTimeout(new LinkedBlockingQueue<String>(), 0)).isEmpty();
        }

        @Test
        @DisplayName("only one element is removed")
        void removesOnlyOne() throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>(Arrays.asList("a", "b"));

            tasks.pollWithTimeout(queue, 100);

            assertThat(queue).containsExactly("b");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement serveByPriority()")
    @DisplayName("serveByPriority")
    class ServeByPriorityTest {

        @Test
        @DisplayName("serves the lowest priority number first (Javadoc example)")
        void example() {
            Map<String, Integer> jobs = new HashMap<>();
            jobs.put("low", 5);
            jobs.put("high", 1);
            jobs.put("mid", 3);

            assertThat(tasks.serveByPriority(jobs)).containsExactly("high", "mid", "low");
        }

        @Test
        @DisplayName("a single job")
        void singleJob() {
            assertThat(tasks.serveByPriority(Collections.singletonMap("only", 9))).containsExactly("only");
        }

        @Test
        @DisplayName("an empty map gives an empty list")
        void emptyMap() {
            assertThat(tasks.serveByPriority(new HashMap<String, Integer>())).isEmpty();
        }

        @Test
        @DisplayName("negative priorities come before positive ones")
        void negativePriorities() {
            Map<String, Integer> jobs = new LinkedHashMap<>();
            jobs.put("b", 0);
            jobs.put("a", -10);
            jobs.put("c", 10);

            assertThat(tasks.serveByPriority(jobs)).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("the input map is not modified")
        void inputUntouched() {
            Map<String, Integer> jobs = new HashMap<>();
            jobs.put("x", 2);
            jobs.put("y", 1);

            tasks.serveByPriority(jobs);

            assertThat(jobs).hasSize(2);
            assertThat(jobs).containsEntry("x", 2);
            assertThat(jobs).containsEntry("y", 1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement processInParallel()")
    @DisplayName("processInParallel")
    @Timeout(10)
    class ProcessInParallelTest {

        @Test
        @DisplayName("upper-cases every job (Javadoc example)")
        void example() throws InterruptedException {
            List<String> result = tasks.processInParallel(Arrays.asList("a", "b", "c"), 2);

            assertThat(result).containsExactlyInAnyOrder("A", "B", "C");
        }

        @Test
        @DisplayName("duplicate jobs are processed once each")
        void duplicates() throws InterruptedException {
            List<String> result = tasks.processInParallel(Arrays.asList("x", "x", "y"), 3);

            assertThat(result).containsExactlyInAnyOrder("X", "X", "Y");
        }

        @Test
        @DisplayName("no jobs: every worker still stops and the result is empty")
        void noJobs() throws InterruptedException {
            assertThat(tasks.processInParallel(Collections.<String>emptyList(), 4)).isEmpty();
        }

        @Test
        @DisplayName("more consumers than jobs")
        void moreConsumersThanJobs() throws InterruptedException {
            assertThat(tasks.processInParallel(Collections.singletonList("only"), 8))
                    .containsExactly("ONLY");
        }

        @Test
        @DisplayName("1000 jobs across 4 consumers are all processed exactly once")
        void manyJobs() throws InterruptedException {
            List<String> jobs = new ArrayList<>();
            List<String> expected = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                jobs.add("job" + i);
                expected.add("JOB" + i);
            }

            List<String> result = tasks.processInParallel(jobs, 4);

            assertThat(result).hasSize(1000);
            assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        @DisplayName("a single consumer")
        void singleConsumer() throws InterruptedException {
            assertThat(tasks.processInParallel(Arrays.asList("a", "b"), 1)).containsExactlyInAnyOrder("A", "B");
        }
    }
}
