package com.vbforge.exception_handling;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TryWithResourcesTasks — your implementations")
class TryWithResourcesTasksTest {

    private TryWithResourcesTasks tasks;
    private List<String> log;

    @BeforeEach
    void setUp() {
        tasks = new TryWithResourcesTasks();
        log = new ArrayList<String>();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement useAndClose()")
    @DisplayName("1. useAndClose(name, log)")
    class UseAndCloseTest {

        @Test
        @DisplayName("example from Javadoc: open, use, close — and \"done\" is returned")
        void basic() {
            String result = tasks.useAndClose("db", log);

            assertThat(result).isEqualTo("done");
            assertThat(log).containsExactly("open:db", "use:db", "close:db");
        }

        @Test
        @DisplayName("the resource name is used in every event")
        void otherName() {
            tasks.useAndClose("cache", log);

            assertThat(log).containsExactly("open:cache", "use:cache", "close:cache");
        }

        @Test
        @DisplayName("the resource is closed exactly once")
        void closedOnce() {
            tasks.useAndClose("db", log);

            assertThat(log).filteredOn("close:db"::equals).hasSize(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement useBoth()")
    @DisplayName("2. useBoth(first, second, log)")
    class UseBothTest {

        @Test
        @DisplayName("example from Javadoc: opened in order, closed in reverse")
        void order() {
            tasks.useBoth("a", "b", log);

            assertThat(log).containsExactly("open:a", "open:b", "use:a", "use:b", "close:b", "close:a");
        }

        @Test
        @DisplayName("names are taken from the arguments")
        void otherNames() {
            tasks.useBoth("in", "out", log);

            assertThat(log).containsExactly("open:in", "open:out", "use:in", "use:out", "close:out", "close:in");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runCatching()")
    @DisplayName("3. runCatching(name, log, body)")
    class RunCatchingTest {

        @Test
        @DisplayName("example from Javadoc: body succeeds")
        void success() {
            String result = tasks.runCatching("r", log, () -> { });

            assertThat(result).isEqualTo("ok");
            assertThat(log).containsExactly("open:r", "close:r");
        }

        @Test
        @DisplayName("example from Javadoc: body fails — closed first, handled afterwards")
        void failure() {
            String result = tasks.runCatching("r", log, () -> {
                throw new IllegalStateException("boom");
            });

            assertThat(result).isEqualTo("failed:boom");
            assertThat(log).containsExactly("open:r", "close:r", "handled:boom");
        }

        @Test
        @DisplayName("the body runs while the resource is open")
        void bodyRunsInsideResourceLifetime() {
            tasks.runCatching("r", log, () -> log.add("body"));

            assertThat(log).containsExactly("open:r", "body", "close:r");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement suppressedMessages()")
    @DisplayName("4. suppressedMessages(exception)")
    class SuppressedMessagesTest {

        @Test
        @DisplayName("example from Javadoc: messages of all suppressed exceptions, in order")
        void severalSuppressed() {
            Exception primary = new Exception("primary");
            primary.addSuppressed(new IllegalStateException("x"));
            primary.addSuppressed(new IllegalStateException("y"));

            assertThat(tasks.suppressedMessages(primary)).containsExactly("x", "y");
        }

        @Test
        @DisplayName("example from Javadoc: nothing suppressed gives an empty list")
        void noneSuppressed() {
            assertThat(tasks.suppressedMessages(new Exception("alone"))).isEmpty();
        }

        @Test
        @DisplayName("works on a real try-with-resources failure")
        void realFailure() {
            Exception e = TryWithResourcesDemo.suppressedDemo(new ArrayList<String>());

            assertThat(tasks.suppressedMessages(e)).containsExactly("close failed: r");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement closeAllQuietly()")
    @DisplayName("5. closeAllQuietly(resources)")
    class CloseAllQuietlyTest {

        @Test
        @DisplayName("example from Javadoc: all are closed even though one fails; failures are counted")
        void oneFailure() {
            List<TrackedResource> resources = Arrays.asList(
                    new TrackedResource("a", log),
                    new TrackedResource("b", log, false, true),
                    new TrackedResource("c", log));

            int failures = tasks.closeAllQuietly(resources);

            assertThat(failures).isEqualTo(1);
            assertThat(log).containsExactly("open:a", "open:b", "open:c", "close:a", "close:b", "close:c");
        }

        @Test
        @DisplayName("example from Javadoc: every resource failing is counted")
        void allFail() {
            List<TrackedResource> resources = Arrays.asList(
                    new TrackedResource("a", log, false, true),
                    new TrackedResource("b", log, false, true));

            assertThat(tasks.closeAllQuietly(resources)).isEqualTo(2);
        }

        @Test
        @DisplayName("example from Javadoc: empty list gives 0")
        void emptyList() {
            assertThat(tasks.closeAllQuietly(Collections.<TrackedResource>emptyList())).isZero();
        }

        @Test
        @DisplayName("null elements are skipped without counting as failures")
        void nullsSkipped() {
            List<TrackedResource> resources = Arrays.asList(null, new TrackedResource("a", log), null);

            assertThat(tasks.closeAllQuietly(resources)).isZero();
            assertThat(log).containsExactly("open:a", "close:a");
        }

        @Test
        @DisplayName("a checked exception from close() is also counted")
        void checkedFailure() {
            AutoCloseable checkedFailing = () -> {
                throw new Exception("checked close failure");
            };

            assertThat(tasks.closeAllQuietly(Arrays.asList(checkedFailing, checkedFailing))).isEqualTo(2);
        }
    }
}
