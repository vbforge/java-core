package com.vbforge.file_io_nio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WatchServiceTasksTest — tests for every method in {@link WatchServiceTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code WatchServiceTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=WatchServiceTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 4 task groups are GREEN.</li>
 * </ol>
 *
 * <p>Tests that expect an event pass a generous timeout ({@value #LONG_MILLIS} ms) — a correct
 * implementation returns as soon as the event arrives. Tests that expect <em>no</em> event use a short timeout.
 */
@DisplayName("WatchServiceTasks — your implementations")
class WatchServiceTasksTest {

    private static final long LONG_MILLIS = 30_000;
    private static final long SHORT_MILLIS = 500;

    private WatchServiceTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new WatchServiceTasks();
    }

    private static Runnable create(Path dir, String... names) {
        return () -> {
            try {
                for (String name : names) {
                    Files.createFile(dir.resolve(name));
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    private static Runnable delete(Path dir, String... names) {
        return () -> {
            try {
                for (String name : names) {
                    Files.delete(dir.resolve(name));
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    // ── Task 1: describeKind ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement describeKind()")
    @DisplayName("Task 1 — describeKind()")
    class DescribeKindTest {

        @Test
        @DisplayName("ENTRY_CREATE → created")
        void created() {
            assertThat(tasks.describeKind(StandardWatchEventKinds.ENTRY_CREATE)).isEqualTo("created");
        }

        @Test
        @DisplayName("ENTRY_MODIFY → modified")
        void modified() {
            assertThat(tasks.describeKind(StandardWatchEventKinds.ENTRY_MODIFY)).isEqualTo("modified");
        }

        @Test
        @DisplayName("ENTRY_DELETE → deleted")
        void deleted() {
            assertThat(tasks.describeKind(StandardWatchEventKinds.ENTRY_DELETE)).isEqualTo("deleted");
        }

        @Test
        @DisplayName("OVERFLOW → overflow")
        void overflow() {
            assertThat(tasks.describeKind(StandardWatchEventKinds.OVERFLOW)).isEqualTo("overflow");
        }

        @Test
        @DisplayName("any other kind throws IllegalArgumentException")
        void unknownKind() {
            WatchEvent.Kind<String> custom = new WatchEvent.Kind<String>() {
                @Override
                public String name() {
                    return "CUSTOM";
                }

                @Override
                public Class<String> type() {
                    return String.class;
                }
            };
            assertThatThrownBy(() -> tasks.describeKind(custom)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 2: waitForCreated ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement waitForCreated()")
    @DisplayName("Task 2 — waitForCreated()")
    class WaitForCreatedTest {

        @Test
        @DisplayName("collects the names of two created files")
        void twoFiles(@TempDir Path tmp) throws Exception {
            List<String> names = tasks.waitForCreated(tmp, create(tmp, "a.txt", "b.txt"), 2, LONG_MILLIS);
            assertThat(names).containsExactlyInAnyOrder("a.txt", "b.txt");
        }

        @Test
        @DisplayName("returns early once enough names arrived (does not wait for the full timeout)")
        void returnsEarly(@TempDir Path tmp) throws Exception {
            long start = System.nanoTime();
            List<String> names = tasks.waitForCreated(tmp, create(tmp, "only.txt"), 1, LONG_MILLIS);
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            assertThat(names).containsExactly("only.txt");
            assertThat(elapsed).isLessThan(LONG_MILLIS - 5_000);
        }

        @Test
        @DisplayName("nothing happens → empty list after roughly the timeout")
        void timeout(@TempDir Path tmp) throws Exception {
            long start = System.nanoTime();
            List<String> names = tasks.waitForCreated(tmp, () -> { }, 1, 300);
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            assertThat(names).isEmpty();
            assertThat(elapsed).isGreaterThanOrEqualTo(250L);
        }

        @Test
        @DisplayName("fewer events than expected → returns what was collected after the timeout")
        void partial(@TempDir Path tmp) throws Exception {
            List<String> names = tasks.waitForCreated(tmp, create(tmp, "one.txt"), 2, SHORT_MILLIS + 1_500);
            assertThat(names).containsExactly("one.txt");
        }

        @Test
        @DisplayName("deletions are not creations")
        void deletesIgnored(@TempDir Path tmp) throws Exception {
            Files.createFile(tmp.resolve("existing.txt"));
            List<String> names = tasks.waitForCreated(tmp, delete(tmp, "existing.txt"), 1, SHORT_MILLIS);
            assertThat(names).isEmpty();
        }

        @Test
        @DisplayName("modifications are not creations")
        void modificationsIgnored(@TempDir Path tmp) throws Exception {
            Path existing = Files.createFile(tmp.resolve("existing.txt"));
            Runnable modify = () -> {
                try {
                    Files.write(existing, new byte[]{1, 2, 3});
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            };
            assertThat(tasks.waitForCreated(tmp, modify, 1, SHORT_MILLIS)).isEmpty();
        }

        @Test
        @DisplayName("a missing directory throws NoSuchFileException")
        void missingDirectory(@TempDir Path tmp) {
            Path missing = tmp.resolve("nope");
            assertThatThrownBy(() -> tasks.waitForCreated(missing, () -> { }, 1, 100))
                    .isInstanceOf(NoSuchFileException.class);
        }
    }

    // ── Task 3: waitForDeletion ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement waitForDeletion()")
    @DisplayName("Task 3 — waitForDeletion()")
    class WaitForDeletionTest {

        @Test
        @DisplayName("the watched file is deleted → true")
        void deleted(@TempDir Path tmp) throws Exception {
            Files.createFile(tmp.resolve("wanted.txt"));
            assertThat(tasks.waitForDeletion(tmp, "wanted.txt", delete(tmp, "wanted.txt"), LONG_MILLIS)).isTrue();
        }

        @Test
        @DisplayName("only another file is deleted → false after the timeout")
        void otherFileDeleted(@TempDir Path tmp) throws Exception {
            Files.createFile(tmp.resolve("wanted.txt"));
            Files.createFile(tmp.resolve("other.txt"));
            assertThat(tasks.waitForDeletion(tmp, "wanted.txt", delete(tmp, "other.txt"), SHORT_MILLIS)).isFalse();
        }

        @Test
        @DisplayName("nothing happens → false after roughly the timeout")
        void nothingHappens(@TempDir Path tmp) throws Exception {
            Files.createFile(tmp.resolve("wanted.txt"));
            long start = System.nanoTime();
            boolean result = tasks.waitForDeletion(tmp, "wanted.txt", () -> { }, 300);
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            assertThat(result).isFalse();
            assertThat(elapsed).isGreaterThanOrEqualTo(250L);
        }

        @Test
        @DisplayName("an unrelated deletion does not end the wait early — the later matching one is found")
        void keepsWaitingAfterOtherDeletion(@TempDir Path tmp) throws Exception {
            Files.createFile(tmp.resolve("wanted.txt"));
            Files.createFile(tmp.resolve("other.txt"));
            Runnable both = delete(tmp, "other.txt", "wanted.txt");
            assertThat(tasks.waitForDeletion(tmp, "wanted.txt", both, LONG_MILLIS)).isTrue();
        }

        @Test
        @DisplayName("creating a file with the watched name is not a deletion")
        void creationIsNotDeletion(@TempDir Path tmp) throws Exception {
            assertThat(tasks.waitForDeletion(tmp, "wanted.txt", create(tmp, "wanted.txt"), SHORT_MILLIS)).isFalse();
        }
    }

    // ── Task 4: registerAll ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement registerAll()")
    @DisplayName("Task 4 — registerAll()")
    class RegisterAllTest {

        private Path tree(Path root) throws IOException {
            Files.createDirectories(root.resolve("a").resolve("b"));
            Files.createDirectory(root.resolve("c"));
            Files.createFile(root.resolve("x.txt"));
            return root;
        }

        @Test
        @DisplayName("counts root + every sub-directory, but no files (4 for the sample tree)")
        void countsDirectories(@TempDir Path tmp) throws IOException {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                assertThat(tasks.registerAll(watcher, tree(tmp))).isEqualTo(4);
            }
        }

        @Test
        @DisplayName("an empty directory registers just itself → 1")
        void emptyDirectory(@TempDir Path tmp) throws IOException {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                assertThat(tasks.registerAll(watcher, tmp)).isEqualTo(1);
            }
        }

        @Test
        @DisplayName("creating files in deep and sibling directories is reported for exactly those directories")
        void eventsFromNestedDirectories(@TempDir Path tmp) throws Exception {
            Path root = tree(tmp);
            Path deep = root.resolve("a").resolve("b");
            Path sibling = root.resolve("c");

            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                tasks.registerAll(watcher, root);

                Files.createFile(deep.resolve("in-deep.txt"));
                Files.createFile(sibling.resolve("in-sibling.txt"));

                List<Object> watched = new ArrayList<>();
                long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(LONG_MILLIS);
                while (watched.size() < 2 && System.nanoTime() < deadline) {
                    WatchKey key = watcher.poll(1, TimeUnit.SECONDS);
                    if (key != null) {
                        watched.add(key.watchable());
                        key.pollEvents();
                        key.reset();
                    }
                }
                assertThat(watched).containsExactlyInAnyOrder(deep, sibling);
            }
        }

        @Test
        @DisplayName("the service is not closed by registerAll")
        void doesNotCloseService(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                tasks.registerAll(watcher, tree(tmp));
                assertThat(watcher.poll()).isNull();           // would throw ClosedWatchServiceException if closed
            }
        }
    }
}
