package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ComposingTasks — your implementations")
class ComposingTasksTest {

    private ComposingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ComposingTasks();
    }

    /** Bounded wait so a future that never completes fails the test instead of hanging it. */
    private static <T> T await(CompletableFuture<T> future) throws Exception {
        return future.get(5, TimeUnit.SECONDS);
    }

    private static <T> CompletableFuture<T> done(T value) {
        return CompletableFuture.completedFuture(value);
    }

    private static <T> CompletableFuture<T> failed(Throwable error) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(error);
        return future;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement asyncTrimUpper()")
    @DisplayName("asyncTrimUpper")
    class AsyncTrimUpperTest {

        @Test
        @DisplayName("example from the Javadoc: '  hi ' → 'HI'")
        void happyPath() throws Exception {
            assertThat(await(tasks.asyncTrimUpper(() -> "  hi "))).isEqualTo("HI");
        }

        @ParameterizedTest(name = "[{index}] ''{0}'' → ''{1}''")
        @CsvSource({
                "'  hi ', HI",
                "Java, JAVA",
                "'   ', ''",
                "'  a b  ', 'A B'"
        })
        @DisplayName("trims and upper-cases")
        void parametrized(String input, String expected) throws Exception {
            assertThat(await(tasks.asyncTrimUpper(() -> input))).isEqualTo(expected);
        }

        @Test
        @DisplayName("the supplier does not run on the calling thread")
        void runsOnAnotherThread() throws Exception {
            AtomicReference<Thread> worker = new AtomicReference<>();

            await(tasks.asyncTrimUpper(() -> {
                worker.set(Thread.currentThread());
                return "x";
            }));

            assertThat(worker.get()).isNotNull().isNotSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("a throwing supplier makes the future fail")
        void failingSupplier() {
            CompletableFuture<String> future = tasks.asyncTrimUpper(() -> {
                throw new IllegalStateException("bad source");
            });

            assertThatThrownBy(() -> await(future))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("bad source");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement emailForUserId()")
    @DisplayName("emailForUserId")
    class EmailForUserIdTest {

        @Test
        @DisplayName("example from the Javadoc: id 7 → user-7@example.com")
        void happyPath() throws Exception {
            CompletableFuture<String> result = tasks.emailForUserId(7,
                    id -> done("user-" + id),
                    name -> done(name + "@example.com"));

            assertThat(await(result)).isEqualTo("user-7@example.com");
        }

        @Test
        @DisplayName("the e-mail lookup receives the name found by the first lookup")
        void secondLookupGetsFirstResult() throws Exception {
            AtomicReference<String> seenName = new AtomicReference<>();

            await(tasks.emailForUserId(3,
                    id -> done("name-of-" + id),
                    name -> {
                        seenName.set(name);
                        return done("mail");
                    }));

            assertThat(seenName.get()).isEqualTo("name-of-3");
        }

        @Test
        @DisplayName("the e-mail lookup only starts after the name lookup has completed")
        void secondLookupWaits() throws Exception {
            CompletableFuture<String> pendingName = new CompletableFuture<>();
            AtomicInteger emailCalls = new AtomicInteger();

            CompletableFuture<String> result = tasks.emailForUserId(1,
                    id -> pendingName,
                    name -> {
                        emailCalls.incrementAndGet();
                        return done(name + "@x.org");
                    });

            assertThat(result).isNotDone();
            assertThat(emailCalls.get()).isZero();

            pendingName.complete("ann");
            assertThat(await(result)).isEqualTo("ann@x.org");
        }

        @Test
        @DisplayName("the result stays pending until the e-mail future completes")
        void resultWaitsForEmail() throws Exception {
            CompletableFuture<String> pendingEmail = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.emailForUserId(1, id -> done("n"), name -> pendingEmail);

            assertThat(result).isNotDone();
            pendingEmail.complete("n@x.org");
            assertThat(await(result)).isEqualTo("n@x.org");
        }

        @Test
        @DisplayName("a failing name lookup fails the result and skips the e-mail lookup")
        void firstLookupFails() {
            AtomicInteger emailCalls = new AtomicInteger();

            CompletableFuture<String> result = tasks.emailForUserId(1,
                    id -> failed(new IllegalStateException("name service down")),
                    name -> {
                        emailCalls.incrementAndGet();
                        return done("never");
                    });

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("name service down");
            assertThat(emailCalls.get()).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement describePerson()")
    @DisplayName("describePerson")
    class DescribePersonTest {

        @Test
        @DisplayName("example from the Javadoc: Alice, 30 → 'Alice (30)'")
        void happyPath() throws Exception {
            assertThat(await(tasks.describePerson(done("Alice"), done(30)))).isEqualTo("Alice (30)");
        }

        @ParameterizedTest(name = "[{index}] {0}, {1} → {0} ({1})")
        @CsvSource({
                "Alice, 30",
                "Bob, 7",
                "Carol, 100"
        })
        @DisplayName("formats name and age")
        void parametrized(String name, int age) throws Exception {
            assertThat(await(tasks.describePerson(done(name), done(age))))
                    .isEqualTo(name + " (" + age + ")");
        }

        @Test
        @DisplayName("completes only after both inputs have completed")
        void waitsForBoth() throws Exception {
            CompletableFuture<String> name = new CompletableFuture<>();
            CompletableFuture<Integer> age = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.describePerson(name, age);

            name.complete("Dan");
            assertThat(result).isNotDone();
            age.complete(41);
            assertThat(await(result)).isEqualTo("Dan (41)");
        }

        @Test
        @DisplayName("fails when the name future fails")
        void nameFails() {
            CompletableFuture<String> result = tasks.describePerson(
                    failed(new IllegalStateException("no name")), done(1));

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("no name");
        }

        @Test
        @DisplayName("fails when the age future fails")
        void ageFails() {
            CompletableFuture<String> result = tasks.describePerson(
                    done("Eve"), failed(new IllegalStateException("no age")));

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("no age");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement recordWhenDone()")
    @DisplayName("recordWhenDone")
    class RecordWhenDoneTest {

        @Test
        @DisplayName("example from the Javadoc: 'hello' → log ['done:hello']")
        void happyPath() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());

            CompletableFuture<Void> result = tasks.recordWhenDone(done("hello"), log);

            assertThat(await(result)).isNull();
            assertThat(log).containsExactly("done:hello");
        }

        @Test
        @DisplayName("nothing is logged while the source is still pending")
        void nothingLoggedWhilePending() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());
            CompletableFuture<String> source = new CompletableFuture<>();

            CompletableFuture<Void> result = tasks.recordWhenDone(source, log);

            assertThat(result).isNotDone();
            assertThat(log).isEmpty();

            source.complete("later");
            await(result);
            assertThat(log).containsExactly("done:later");
        }

        @Test
        @DisplayName("the returned future completes only after the entry is written")
        void completesAfterWrite() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<Void> result = tasks.recordWhenDone(source, log);

            source.complete("x");
            await(result);

            assertThat(log).hasSize(1);
        }

        @Test
        @DisplayName("a failed source logs nothing and fails the returned future")
        void failedSource() {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());

            CompletableFuture<Void> result = tasks.recordWhenDone(
                    failed(new IllegalStateException("source broke")), log);

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("source broke");
            assertThat(log).isEmpty();
        }
    }
}
