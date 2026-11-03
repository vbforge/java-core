package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("ExecutorServiceDemo — API contracts")
@Timeout(value = 20, unit = TimeUnit.SECONDS)
class ExecutorServiceDemoTest {

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void stopNow(ExecutorService... pools) {
        for (ExecutorService pool : pools) {
            pool.shutdownNow();
        }
    }

    @Nested
    @DisplayName("1. Fixed thread pool")
    class FixedPool {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::fixedPoolDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("A pool of 3 never uses more than 3 distinct threads")
        void neverMoreThanPoolSizeThreads() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(3);
            try {
                Set<String> names = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                List<Future<?>> futures = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    futures.add(pool.submit(() -> names.add(Thread.currentThread().getName())));
                }
                for (Future<?> future : futures) {
                    future.get();
                }
                assertThat(names).isNotEmpty();
                assertThat(names.size()).isLessThanOrEqualTo(3);
            } finally {
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("Default threads are named pool-N-thread-M and are not daemon threads")
        void defaultThreadsAreNamedAndNonDaemon() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            try {
                Future<String> name = pool.submit(() -> Thread.currentThread().getName());
                Future<Boolean> daemon = pool.submit(() -> Thread.currentThread().isDaemon());
                assertThat(name.get()).matches("pool-\\d+-thread-\\d+");
                assertThat(daemon.get()).isFalse();
            } finally {
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("Tasks beyond the pool size wait in the queue until a worker is free")
        void extraTasksWaitInQueue() throws Exception {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
            CountDownLatch release = new CountDownLatch(1);
            try {
                pool.execute(() -> awaitQuietly(release));
                pool.execute(() -> awaitQuietly(release));
                pool.execute(() -> awaitQuietly(release));
                assertThat(pool.getPoolSize()).isEqualTo(1);
                assertThat(pool.getQueue().size()).isEqualTo(2);
            } finally {
                release.countDown();
                stopNow(pool);
            }
        }
    }

    @Nested
    @DisplayName("2. Single-thread and cached pools")
    class PoolFactories {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::poolFactoriesDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Single-thread executor runs tasks in submission order")
        void singleThreadKeepsOrder() throws Exception {
            ExecutorService single = Executors.newSingleThreadExecutor();
            List<Integer> order = Collections.synchronizedList(new ArrayList<Integer>());
            for (int i = 0; i < 5; i++) {
                final int step = i;
                single.execute(() -> order.add(step));
            }
            single.shutdown();
            assertThat(single.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactly(0, 1, 2, 3, 4);
        }

        @Test
        @DisplayName("Single-thread executor uses exactly one thread")
        void singleThreadUsesOneThread() throws Exception {
            ExecutorService single = Executors.newSingleThreadExecutor();
            try {
                Set<String> names = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                List<Future<?>> futures = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    futures.add(single.submit(() -> names.add(Thread.currentThread().getName())));
                }
                for (Future<?> future : futures) {
                    future.get();
                }
                assertThat(names).hasSize(1);
            } finally {
                stopNow(single);
            }
        }

        @Test
        @DisplayName("Cached pool runs 5 blocking tasks at the same time (no queueing)")
        void cachedPoolGrowsOnDemand() throws Exception {
            ExecutorService cached = Executors.newCachedThreadPool();
            CountDownLatch allRunning = new CountDownLatch(5);
            CountDownLatch release = new CountDownLatch(1);
            try {
                for (int i = 0; i < 5; i++) {
                    cached.execute(() -> {
                        allRunning.countDown();
                        awaitQuietly(release);
                    });
                }
                assertThat(allRunning.await(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                release.countDown();
                stopNow(cached);
            }
        }
    }

    @Nested
    @DisplayName("3. Scheduled pool")
    class ScheduledPool {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::scheduledPoolDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("schedule() returns its value only after the delay")
        void scheduleRespectsDelay() throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                long start = System.nanoTime();
                ScheduledFuture<String> future = scheduler.schedule(() -> "later", 60, TimeUnit.MILLISECONDS);
                assertThat(future.get()).isEqualTo("later");
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                assertThat(elapsedMillis).isGreaterThanOrEqualTo(50);
            } finally {
                stopNow(scheduler);
            }
        }

        @Test
        @DisplayName("A fixed-rate task runs repeatedly until it is cancelled")
        void fixedRateRunsUntilCancelled() throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                CountDownLatch threeRuns = new CountDownLatch(3);
                ScheduledFuture<?> periodic =
                        scheduler.scheduleAtFixedRate(threeRuns::countDown, 0, 10, TimeUnit.MILLISECONDS);
                assertThat(threeRuns.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(periodic.cancel(false)).isTrue();
                assertThat(periodic.isCancelled()).isTrue();
                assertThatThrownBy(periodic::get).isInstanceOf(CancellationException.class);
            } finally {
                stopNow(scheduler);
            }
        }

        @Test
        @DisplayName("An exception in a periodic task silently cancels all further runs")
        void exceptionStopsPeriodicTask() throws Exception {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                AtomicInteger runs = new AtomicInteger();
                ScheduledFuture<?> periodic = scheduler.scheduleAtFixedRate(() -> {
                    runs.incrementAndGet();
                    throw new IllegalStateException("tick failed");
                }, 0, 10, TimeUnit.MILLISECONDS);

                assertThatThrownBy(periodic::get)
                        .isInstanceOf(ExecutionException.class)
                        .hasCauseInstanceOf(IllegalStateException.class);
                Thread.sleep(100); // ten periods would have passed
                assertThat(runs.get()).isEqualTo(1);
            } finally {
                stopNow(scheduler);
            }
        }
    }

    @Nested
    @DisplayName("4. shutdown() vs shutdownNow()")
    class Shutdown {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::shutdownDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("After shutdown() new tasks are rejected")
        void rejectsNewTasksAfterShutdown() {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            pool.shutdown();
            assertThat(pool.isShutdown()).isTrue();
            assertThatThrownBy(() -> pool.execute(() -> { }))
                    .isInstanceOf(RejectedExecutionException.class);
        }

        @Test
        @DisplayName("shutdown() lets queued tasks finish")
        void shutdownLetsQueuedTasksFinish() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            CountDownLatch release = new CountDownLatch(1);
            AtomicBoolean queuedTaskRan = new AtomicBoolean(false);
            pool.execute(() -> awaitQuietly(release));
            pool.execute(() -> queuedTaskRan.set(true));

            pool.shutdown();
            assertThat(pool.isTerminated()).isFalse();

            release.countDown();
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            assertThat(pool.isTerminated()).isTrue();
            assertThat(queuedTaskRan.get()).isTrue();
        }

        @Test
        @DisplayName("shutdownNow() interrupts the running task and returns the queued ones")
        void shutdownNowInterruptsAndReturnsQueued() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch neverReleased = new CountDownLatch(1);
            CountDownLatch interrupted = new CountDownLatch(1);
            pool.execute(() -> {
                started.countDown();
                try {
                    neverReleased.await();
                } catch (InterruptedException e) {
                    interrupted.countDown();
                }
            });
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            pool.execute(() -> { });
            pool.execute(() -> { });

            List<Runnable> neverStarted = pool.shutdownNow();

            assertThat(neverStarted).hasSize(2);
            assertThat(interrupted.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }

        @Test
        @DisplayName("shutdownNow() cannot stop a task that ignores interrupts")
        void shutdownNowCannotStopStubbornTask() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            CountDownLatch started = new CountDownLatch(1);
            AtomicBoolean stop = new AtomicBoolean(false);
            pool.execute(() -> {
                started.countDown();
                while (!stop.get()) {
                    Thread.yield(); // never checks the interrupt flag
                }
            });
            try {
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
                pool.shutdownNow();
                assertThat(pool.awaitTermination(100, TimeUnit.MILLISECONDS)).isFalse();
            } finally {
                stop.set(true);
            }
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @DisplayName("5. ThreadPoolExecutor: core, queue, max")
    class ThreadPoolExecutorSizing {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::threadPoolExecutorDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Core 1, max 2, queue 1: thread 1, queue, thread 2, then rejection")
        void decisionOrder() {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1));
            try {
                pool.execute(() -> awaitQuietly(release));
                assertThat(pool.getPoolSize()).isEqualTo(1);
                assertThat(pool.getQueue()).isEmpty();

                pool.execute(() -> awaitQuietly(release));
                assertThat(pool.getPoolSize()).isEqualTo(1);
                assertThat(pool.getQueue()).hasSize(1);

                pool.execute(() -> awaitQuietly(release));
                assertThat(pool.getPoolSize()).isEqualTo(2);
                assertThat(pool.getQueue()).hasSize(1);

                assertThatThrownBy(() -> pool.execute(() -> awaitQuietly(release)))
                        .isInstanceOf(RejectedExecutionException.class);
            } finally {
                release.countDown();
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("The default rejection handler is AbortPolicy")
        void defaultHandlerIsAbortPolicy() {
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1));
            try {
                assertThat(pool.getRejectedExecutionHandler())
                        .isInstanceOf(ThreadPoolExecutor.AbortPolicy.class);
            } finally {
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("With an unbounded queue the pool never grows past the core size")
        void unboundedQueueNeverGrowsPastCore() {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 5, 30, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
            try {
                for (int i = 0; i < 4; i++) {
                    pool.execute(() -> awaitQuietly(release));
                }
                assertThat(pool.getPoolSize()).isEqualTo(1);
                assertThat(pool.getQueue()).hasSize(3);
            } finally {
                release.countDown();
                stopNow(pool);
            }
        }
    }

    @Nested
    @DisplayName("6. Rejection policies")
    class RejectionPolicies {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::rejectionPolicyDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("CallerRunsPolicy runs the rejected task on the submitting thread")
        void callerRunsPolicy() {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
            try {
                pool.execute(() -> awaitQuietly(release)); // occupies the worker
                pool.execute(() -> awaitQuietly(release)); // fills the queue
                AtomicReference<Thread> ranOn = new AtomicReference<>();
                pool.execute(() -> ranOn.set(Thread.currentThread()));
                assertThat(ranOn.get()).isSameAs(Thread.currentThread());
            } finally {
                release.countDown();
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("DiscardPolicy drops the task silently: no exception and it never runs")
        void discardPolicy() throws Exception {
            CountDownLatch release = new CountDownLatch(1);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardPolicy());
            AtomicInteger discardedRuns = new AtomicInteger();
            try {
                pool.execute(() -> awaitQuietly(release));
                pool.execute(() -> awaitQuietly(release));
                assertThatCode(() -> pool.execute(discardedRuns::incrementAndGet))
                        .doesNotThrowAnyException();
            } finally {
                release.countDown();
                pool.shutdown();
            }
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            assertThat(discardedRuns.get()).isZero();
        }
    }

    @Nested
    @DisplayName("7. Custom ThreadFactory")
    class CustomThreadFactory {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::threadFactoryDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("A factory controls thread names and the daemon flag")
        void factoryControlsNamesAndDaemonFlag() throws Exception {
            AtomicInteger counter = new AtomicInteger(1);
            ThreadFactory factory = runnable -> {
                Thread thread = new Thread(runnable, "order-worker-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            };
            ExecutorService pool = Executors.newFixedThreadPool(2, factory);
            try {
                Future<String> first = pool.submit(
                        () -> Thread.currentThread().getName() + "/" + Thread.currentThread().isDaemon());
                Future<String> second = pool.submit(
                        () -> Thread.currentThread().getName() + "/" + Thread.currentThread().isDaemon());
                assertThat(first.get()).isNotEqualTo(second.get());
                assertThat(first.get()).endsWith("/true").startsWith("order-worker-");
                assertThat(second.get()).endsWith("/true").startsWith("order-worker-");
            } finally {
                stopNow(pool);
            }
        }

        @Test
        @DisplayName("The first corePoolSize tasks each get their own new thread")
        void firstTasksGetSeparateThreads() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Future<String> first = pool.submit(() -> Thread.currentThread().getName());
                first.get(); // the first worker is idle again now
                Future<String> second = pool.submit(() -> Thread.currentThread().getName());
                assertThat(second.get()).isNotEqualTo(first.get());
            } finally {
                stopNow(pool);
            }
        }
    }

    @Nested
    @DisplayName("8. Gotchas of the Executors factories")
    class FactoryGotchas {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(ExecutorServiceDemo::factoryGotchasDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("newFixedThreadPool uses an unbounded queue")
        void fixedPoolQueueIsUnbounded() {
            ThreadPoolExecutor fixed = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
            try {
                assertThat(fixed.getQueue().remainingCapacity()).isEqualTo(Integer.MAX_VALUE);
            } finally {
                stopNow(fixed);
            }
        }

        @Test
        @DisplayName("newCachedThreadPool allows Integer.MAX_VALUE threads")
        void cachedPoolHasNoThreadLimit() {
            ThreadPoolExecutor cached = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            try {
                assertThat(cached.getCorePoolSize()).isZero();
                assertThat(cached.getMaximumPoolSize()).isEqualTo(Integer.MAX_VALUE);
            } finally {
                stopNow(cached);
            }
        }
    }
}
