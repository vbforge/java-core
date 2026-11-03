package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ExecutorServiceTasks — your implementations")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class ExecutorServiceTasksTest {

    private ExecutorServiceTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ExecutorServiceTasks();
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Set<Thread> livePoolThreads() {
        Set<Thread> result = new HashSet<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.isAlive() && thread.getName().startsWith("pool-")) {
                result.add(thread);
            }
        }
        return result;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement squareAll()")
    @DisplayName("squareAll")
    class SquareAllTest {

        @Test
        @DisplayName("Squares [1, 2, 3, 4] in input order")
        void example() throws Exception {
            assertThat(tasks.squareAll(Arrays.asList(1, 2, 3, 4), 2)).containsExactly(1, 4, 9, 16);
        }

        @Test
        @DisplayName("Empty input gives an empty result")
        void empty() throws Exception {
            assertThat(tasks.squareAll(Collections.<Integer>emptyList(), 3)).isEmpty();
        }

        @Test
        @DisplayName("Pool size larger than the input still works")
        void poolLargerThanInput() throws Exception {
            assertThat(tasks.squareAll(Arrays.asList(3), 10)).containsExactly(9);
        }

        @Test
        @DisplayName("Handles negative numbers")
        void negativeNumbers() throws Exception {
            assertThat(tasks.squareAll(Arrays.asList(-2, 0, 5), 2)).containsExactly(4, 0, 25);
        }

        @Test
        @DisplayName("A pool size below 1 throws IllegalArgumentException")
        void invalidPoolSize() {
            assertThatThrownBy(() -> tasks.squareAll(Arrays.asList(5), 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Returns a new list, not the input")
        void returnsNewList() throws Exception {
            List<Integer> input = Arrays.asList(1, 2);
            assertThat(tasks.squareAll(input, 2)).isNotSameAs(input);
        }

        @ParameterizedTest(name = "{0} numbers on {1} threads")
        @CsvSource({"1,1", "10,3", "50,4", "7,20"})
        @DisplayName("Keeps input order for many sizes")
        void keepsOrder(int count, int poolSize) throws Exception {
            List<Integer> input = new ArrayList<>();
            List<Integer> expected = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                input.add(i);
                expected.add(i * i);
            }
            assertThat(tasks.squareAll(input, poolSize)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Shuts its pool down: no worker threads are left running")
        void shutsPoolDown() throws Exception {
            Set<Thread> before = livePoolThreads();

            tasks.squareAll(Arrays.asList(1, 2, 3), 2);

            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            Set<Thread> leaked;
            do {
                leaked = livePoolThreads();
                leaked.removeAll(before);
                if (leaked.isEmpty()) {
                    break;
                }
                Thread.sleep(20);
            } while (System.nanoTime() < deadline);
            assertThat(leaked).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement shutdownGracefully()")
    @DisplayName("shutdownGracefully")
    class ShutdownGracefullyTest {

        @Test
        @DisplayName("Quick tasks: returns true and the executor is terminated")
        void quickTasks() throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                for (int i = 0; i < 4; i++) {
                    executor.execute(() -> { });
                }
                assertThat(tasks.shutdownGracefully(executor, 1000)).isTrue();
                assertThat(executor.isShutdown()).isTrue();
                assertThat(executor.isTerminated()).isTrue();
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("A task that never ends: returns false, interrupts the task, executor stops")
        void neverEndingTask() throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch neverReleased = new CountDownLatch(1);
            CountDownLatch interrupted = new CountDownLatch(1);
            try {
                executor.execute(() -> {
                    started.countDown();
                    try {
                        neverReleased.await();
                    } catch (InterruptedException e) {
                        interrupted.countDown();
                    }
                });
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

                assertThat(tasks.shutdownGracefully(executor, 50)).isFalse();

                assertThat(executor.isShutdown()).isTrue();
                assertThat(interrupted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("An idle executor shuts down gracefully")
        void idleExecutor() {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                assertThat(tasks.shutdownGracefully(executor, 1000)).isTrue();
                assertThat(executor.isTerminated()).isTrue();
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("Queued tasks still run when they fit into the timeout")
        void queuedTasksStillRun() {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AtomicBoolean lastRan = new AtomicBoolean(false);
            try {
                executor.execute(() -> {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                executor.execute(() -> lastRan.set(true));

                assertThat(tasks.shutdownGracefully(executor, 5000)).isTrue();
                assertThat(lastRan.get()).isTrue();
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("If the caller is interrupted: forces the shutdown, keeps the interrupt flag, returns false")
        void callerInterrupted() throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch neverReleased = new CountDownLatch(1);
            try {
                executor.execute(() -> {
                    started.countDown();
                    awaitQuietly(neverReleased);
                });
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

                Thread.currentThread().interrupt();
                boolean result = tasks.shutdownGracefully(executor, 10_000);
                boolean flagStillSet = Thread.interrupted(); // reads and clears the flag

                assertThat(result).isFalse();
                assertThat(flagStillSet).isTrue();
                assertThat(executor.isShutdown()).isTrue();
                assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                Thread.interrupted();
                executor.shutdownNow();
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement newNamedFixedPool()")
    @DisplayName("newNamedFixedPool")
    class NewNamedFixedPoolTest {

        @Test
        @DisplayName("newNamedFixedPool(2, \"report\") names its threads report-1 and report-2")
        void example() throws Exception {
            ExecutorService pool = tasks.newNamedFixedPool(2, "report");
            try {
                Future<String> first = pool.submit(() -> Thread.currentThread().getName());
                first.get();
                Future<String> second = pool.submit(() -> Thread.currentThread().getName());
                assertThat(Arrays.asList(first.get(), second.get()))
                        .containsExactlyInAnyOrder("report-1", "report-2");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("newNamedFixedPool(1, \"io\") names its only thread io-1, for every task")
        void singleThread() throws Exception {
            ExecutorService pool = tasks.newNamedFixedPool(1, "io");
            try {
                assertThat(pool.submit(() -> Thread.currentThread().getName()).get()).isEqualTo("io-1");
                assertThat(pool.submit(() -> Thread.currentThread().getName()).get()).isEqualTo("io-1");
            } finally {
                pool.shutdownNow();
            }
        }

        @ParameterizedTest(name = "{0} threads named {1}-N")
        @CsvSource({"1,io", "2,report", "3,worker", "5,db-pool"})
        @DisplayName("Creates exactly size threads, numbered from 1 in creation order")
        void namesAreNumbered(int size, String prefix) throws Exception {
            ExecutorService pool = tasks.newNamedFixedPool(size, prefix);
            CountDownLatch allRunning = new CountDownLatch(size);
            CountDownLatch release = new CountDownLatch(1);
            Set<String> names = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
            try {
                for (int i = 0; i < size; i++) {
                    pool.execute(() -> {
                        names.add(Thread.currentThread().getName());
                        allRunning.countDown();
                        awaitQuietly(release);
                    });
                }
                assertThat(allRunning.await(5, TimeUnit.SECONDS)).isTrue();

                Set<String> expected = new HashSet<>();
                for (int i = 1; i <= size; i++) {
                    expected.add(prefix + "-" + i);
                }
                assertThat(names).isEqualTo(expected);
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("It is a fixed pool: a 3rd task waits while 2 threads are busy")
        void isFixedPool() throws Exception {
            ExecutorService pool = tasks.newNamedFixedPool(2, "fixed");
            CountDownLatch busy = new CountDownLatch(2);
            CountDownLatch release = new CountDownLatch(1);
            CountDownLatch thirdStarted = new CountDownLatch(1);
            try {
                for (int i = 0; i < 2; i++) {
                    pool.execute(() -> {
                        busy.countDown();
                        awaitQuietly(release);
                    });
                }
                assertThat(busy.await(5, TimeUnit.SECONDS)).isTrue();
                pool.execute(thirdStarted::countDown);

                assertThat(thirdStarted.await(100, TimeUnit.MILLISECONDS)).isFalse();

                release.countDown();
                assertThat(thirdStarted.await(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement newBoundedPool()")
    @DisplayName("newBoundedPool")
    class NewBoundedPoolTest {

        @Test
        @DisplayName("Core 1, max 2, queue 1: three tasks are accepted, the 4th is rejected")
        void example() {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = tasks.newBoundedPool(1, 2, 1);
            try {
                pool.execute(() -> awaitQuietly(release));
                pool.execute(() -> awaitQuietly(release));
                pool.execute(() -> awaitQuietly(release));
                assertThat(pool.getPoolSize()).isEqualTo(2);
                assertThat(pool.getQueue()).hasSize(1);

                assertThatThrownBy(() -> pool.execute(() -> awaitQuietly(release)))
                        .isInstanceOf(RejectedExecutionException.class);
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("Reports the configured core size, maximum size and queue capacity")
        void configuration() {
            ThreadPoolExecutor pool = tasks.newBoundedPool(2, 5, 7);
            try {
                assertThat(pool.getCorePoolSize()).isEqualTo(2);
                assertThat(pool.getMaximumPoolSize()).isEqualTo(5);
                assertThat(pool.getQueue().remainingCapacity()).isEqualTo(7);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("Idle extra threads are reclaimed after 30 seconds")
        void keepAlive() {
            ThreadPoolExecutor pool = tasks.newBoundedPool(1, 2, 1);
            try {
                assertThat(pool.getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(30);
            } finally {
                pool.shutdownNow();
            }
        }

        @ParameterizedTest(name = "core={0}, max={1}, queue={2} accepts {3} blocking tasks")
        @CsvSource({"1,1,1,2", "1,2,1,3", "2,2,3,5", "2,4,2,6"})
        @DisplayName("Accepts exactly maxSize + queueCapacity blocking tasks, then rejects")
        void capacity(int core, int max, int queue, int accepted) {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = tasks.newBoundedPool(core, max, queue);
            try {
                int count = 0;
                boolean rejected = false;
                for (int i = 0; i < 20 && !rejected; i++) {
                    try {
                        pool.execute(() -> awaitQuietly(release));
                        count++;
                    } catch (RejectedExecutionException e) {
                        rejected = true;
                    }
                }
                assertThat(rejected).isTrue();
                assertThat(count).isEqualTo(accepted);
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement scheduleGreeting()")
    @DisplayName("scheduleGreeting")
    class ScheduleGreetingTest {

        @Test
        @DisplayName("scheduleGreeting(scheduler, \"Ann\", 20) completes with \"Hello, Ann!\"")
        void example() throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                ScheduledFuture<String> future = tasks.scheduleGreeting(scheduler, "Ann", 20);
                assertThat(future.get(5, TimeUnit.SECONDS)).isEqualTo("Hello, Ann!");
            } finally {
                scheduler.shutdownNow();
            }
        }

        @Test
        @DisplayName("Returns immediately: with a 2000 ms delay the future is not done yet")
        void returnsImmediately() {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                long start = System.nanoTime();
                ScheduledFuture<String> future = tasks.scheduleGreeting(scheduler, "Bob", 2000);
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                assertThat(elapsedMillis).isLessThan(1000);
                assertThat(future.isDone()).isFalse();
                assertThat(future.cancel(true)).isTrue();
            } finally {
                scheduler.shutdownNow();
            }
        }

        @Test
        @DisplayName("The greeting is produced no earlier than the delay")
        void respectsDelay() throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                long start = System.nanoTime();
                tasks.scheduleGreeting(scheduler, "Cy", 80).get(5, TimeUnit.SECONDS);
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                assertThat(elapsedMillis).isGreaterThanOrEqualTo(70);
            } finally {
                scheduler.shutdownNow();
            }
        }

        @ParameterizedTest(name = "greets {0}")
        @CsvSource({"Ann", "Bob", "Zoë", "Mary Jane"})
        @DisplayName("Builds the greeting from the given name")
        void greetsByName(String name) throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                assertThat(tasks.scheduleGreeting(scheduler, name, 0).get(5, TimeUnit.SECONDS))
                        .isEqualTo("Hello, " + name + "!");
            } finally {
                scheduler.shutdownNow();
            }
        }
    }
}
