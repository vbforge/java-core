package com.vbforge.virtual_threads_intro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("VirtualThreadBasicsDemo — API contracts")
class VirtualThreadBasicsDemoTest {

    @Nested
    @DisplayName("1. Creating virtual threads")
    class CreatingVirtualThreads {

        @Test
        @DisplayName("builder: named virtual thread runs the code on a virtual thread")
        void builderStartsNamedVirtualThread() throws Exception {
            AtomicReference<Thread> seen = new AtomicReference<>();

            Thread t = Thread.ofVirtual().name("greeter").start(() -> seen.set(Thread.currentThread()));
            t.join();

            assertThat(seen.get()).isSameAs(t);
            assertThat(t.isVirtual()).isTrue();
            assertThat(t.getName()).isEqualTo("greeter");
        }

        @Test
        @DisplayName("startVirtualThread: unnamed thread has the empty string as name")
        void startVirtualThreadIsUnnamed() throws Exception {
            AtomicReference<String> name = new AtomicReference<>("not-set");

            Thread.startVirtualThread(() -> name.set(Thread.currentThread().getName())).join();

            assertThat(name.get()).isEmpty();
        }

        @Test
        @DisplayName("unstarted: state is NEW until start(), TERMINATED after join()")
        void unstartedThreadLifecycle() throws Exception {
            AtomicInteger runs = new AtomicInteger();
            Thread t = Thread.ofVirtual().unstarted(runs::incrementAndGet);

            assertThat(t.getState()).isEqualTo(Thread.State.NEW);
            assertThat(runs.get()).isZero();

            t.start();
            t.join();

            assertThat(t.getState()).isEqualTo(Thread.State.TERMINATED);
            assertThat(runs.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("factory: name(prefix, start) numbers the threads it creates")
        void factoryNumbersThreads() {
            ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();

            Thread first = factory.newThread(() -> { });
            Thread second = factory.newThread(() -> { });

            assertThat(first.getName()).isEqualTo("worker-0");
            assertThat(second.getName()).isEqualTo("worker-1");
            assertThat(first.isVirtual()).isTrue();
        }

        @Test
        @DisplayName("platform builder creates a non-virtual thread; the test thread is not virtual either")
        void platformThreadsAreNotVirtual() {
            assertThat(Thread.ofPlatform().unstarted(() -> { }).isVirtual()).isFalse();
            assertThat(Thread.currentThread().isVirtual()).isFalse();
        }
    }

    @Nested
    @DisplayName("2. Properties of virtual threads")
    class Properties {

        @Test
        @DisplayName("virtual threads are always daemon threads")
        void alwaysDaemon() {
            Thread t = Thread.ofVirtual().unstarted(() -> { });

            assertThat(t.isDaemon()).isTrue();
        }

        @Test
        @DisplayName("setDaemon(false) is rejected with IllegalArgumentException")
        void cannotBecomeNonDaemon() {
            Thread t = Thread.ofVirtual().unstarted(() -> { });

            assertThatThrownBy(() -> t.setDaemon(false)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("priority is fixed at NORM_PRIORITY — setPriority is ignored")
        void priorityIsFixed() {
            Thread t = Thread.ofVirtual().unstarted(() -> { });

            t.setPriority(Thread.MAX_PRIORITY);

            assertThat(t.getPriority()).isEqualTo(Thread.NORM_PRIORITY);
        }

        @Test
        @DisplayName("default name is empty and the group is 'VirtualThreads'")
        void defaultNameAndGroup() {
            Thread t = Thread.ofVirtual().unstarted(() -> { });

            assertThat(t.getName()).isEmpty();
            assertThat(t.getThreadGroup().getName()).isEqualTo("VirtualThreads");
        }

        @Test
        @DisplayName("inside a virtual thread, currentThread() is the virtual thread itself")
        void currentThreadIsVirtual() throws Exception {
            AtomicReference<Boolean> virtual = new AtomicReference<>();

            Thread.startVirtualThread(() -> virtual.set(Thread.currentThread().isVirtual())).join();

            assertThat(virtual.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("3. One virtual thread per task")
    class ExecutorPerTask {

        @Test
        @DisplayName("close() waits until every submitted task has finished")
        void closeWaitsForTasks() {
            AtomicInteger completed = new AtomicInteger();

            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try (executor) {
                for (int i = 0; i < 10; i++) {
                    executor.submit(() -> {
                        Thread.sleep(20);
                        return completed.incrementAndGet();
                    });
                }
            }

            assertThat(completed.get()).isEqualTo(10);
            assertThat(executor.isShutdown()).isTrue();
            assertThat(executor.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("every task gets its own, never reused, virtual thread")
        void everyTaskGetsNewThread() {
            Set<Thread> threads = ConcurrentHashMap.newKeySet();

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 50; i++) {
                    executor.submit(() -> threads.add(Thread.currentThread()));
                }
            }

            assertThat(threads).hasSize(50);
            assertThat(threads).allMatch(Thread::isVirtual);
        }

        @Test
        @DisplayName("submit returns a Future whose get() delivers the task's result")
        void futureDeliversResult() throws Exception {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                assertThat(executor.submit(() -> "done").get()).isEqualTo("done");
            }
        }
    }

    @Nested
    @DisplayName("4. Massive concurrency")
    class MassiveConcurrency {

        @Test
        @DisplayName("10_000 virtual threads are alive and blocked at the same time")
        void tenThousandAtOnce() throws Exception {
            assertThat(VirtualThreadBasicsDemo.simultaneouslyBlocked(10_000)).isEqualTo(10_000);
        }

        @Test
        @DisplayName("small and empty inputs work too")
        void smallInputs() throws Exception {
            assertThat(VirtualThreadBasicsDemo.simultaneouslyBlocked(1)).isEqualTo(1);
            assertThat(VirtualThreadBasicsDemo.simultaneouslyBlocked(0)).isZero();
        }
    }
}
