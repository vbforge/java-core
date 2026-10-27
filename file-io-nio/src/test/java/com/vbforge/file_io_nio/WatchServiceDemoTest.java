package com.vbforge.file_io_nio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * WatchServiceDemoTest — verifies every concept shown in {@link WatchServiceDemo}.
 *
 * <p>These tests are always GREEN. Where a test <em>expects</em> an event it waits up to
 * {@value #WAIT_SECONDS} seconds and returns as soon as the event arrives (instantly on Linux and
 * Windows; up to ~10 s on macOS, where the JDK polls). Where a test expects <em>no</em> event it
 * waits a short fixed time, which is deterministic on every platform.
 */
@DisplayName("WatchServiceDemo — API contracts")
class WatchServiceDemoTest {

    static final long WAIT_SECONDS = 30;
    private static final long QUIET_MILLIS = 500;

    private static WatchService newWatcher() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }

    // ── 1. The basic loop ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The basic loop")
    class BasicLoopTest {

        @Test
        @DisplayName("creating a file yields ENTRY_CREATE with the relative file name as context")
        void createEvent(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                Files.createFile(tmp.resolve("hello.txt"));

                WatchKey key = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
                assertThat(key).isNotNull();

                List<WatchEvent<?>> events = key.pollEvents();
                assertThat(events).isNotEmpty();
                for (WatchEvent<?> event : events) {
                    assertThat(event.kind()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE);
                    Path context = (Path) event.context();
                    assertThat(context).isEqualTo(Paths.get("hello.txt"));
                    assertThat(context.isAbsolute()).isFalse();
                }
            }
        }

        @Test
        @DisplayName("key.watchable() is the registered directory; resolve(context) is the full path")
        void watchableAndResolve(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                Files.createFile(tmp.resolve("hello.txt"));

                WatchKey key = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
                assertThat(key).isNotNull();
                Path watched = (Path) key.watchable();
                assertThat(watched).isEqualTo(tmp);

                Path context = (Path) key.pollEvents().get(0).context();
                assertThat(watched.resolve(context)).isEqualTo(tmp.resolve("hello.txt"));
            }
        }

        @Test
        @DisplayName("deleting a file yields ENTRY_DELETE")
        void deleteEvent(@TempDir Path tmp) throws Exception {
            Path file = Files.createFile(tmp.resolve("gone.txt"));
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
                Files.delete(file);

                WatchKey key = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
                assertThat(key).isNotNull();
                WatchEvent<?> event = key.pollEvents().get(0);
                assertThat(event.kind()).isEqualTo(StandardWatchEventKinds.ENTRY_DELETE);
                assertThat(event.context()).isEqualTo(Paths.get("gone.txt"));
            }
        }

        @Test
        @DisplayName("poll() returns null at once; poll(timeout) returns null after waiting")
        void pollReturnsNullWhenQuiet(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                assertThat(watcher.poll()).isNull();

                long start = System.nanoTime();
                assertThat(watcher.poll(200, TimeUnit.MILLISECONDS)).isNull();
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                assertThat(elapsedMillis).isGreaterThanOrEqualTo(150L);
            }
        }
    }

    // ── 2. Event kinds and limits ─────────────────────────────────────────────

    @Nested
    @DisplayName("2. Event kinds and limits")
    class EventKindsTest {

        @Test
        @DisplayName("the four standard kinds have the expected names")
        void kindNames() {
            assertThat(StandardWatchEventKinds.ENTRY_CREATE.name()).isEqualTo("ENTRY_CREATE");
            assertThat(StandardWatchEventKinds.ENTRY_MODIFY.name()).isEqualTo("ENTRY_MODIFY");
            assertThat(StandardWatchEventKinds.ENTRY_DELETE.name()).isEqualTo("ENTRY_DELETE");
            assertThat(StandardWatchEventKinds.OVERFLOW.name()).isEqualTo("OVERFLOW");
        }

        @Test
        @DisplayName("only registered kinds are reported — a CREATE is invisible to a DELETE-only key")
        void unregisteredKindIgnored(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
                Files.createFile(tmp.resolve("ignored.txt"));
                assertThat(watcher.poll(QUIET_MILLIS, TimeUnit.MILLISECONDS)).isNull();
            }
        }

        @Test
        @DisplayName("registering the same directory twice returns the same key")
        void reRegisterReturnsSameKey(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                WatchKey first = tmp.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
                WatchKey second = tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                assertThat(second).isSameAs(first);
            }
        }

        @Test
        @DisplayName("watching is not recursive — a file created in a sub-directory is invisible")
        void notRecursive(@TempDir Path tmp) throws Exception {
            Path child = Files.createDirectory(tmp.resolve("child"));
            try (WatchService watcher = newWatcher()) {
                tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                Files.createFile(child.resolve("nested.txt"));
                assertThat(watcher.poll(QUIET_MILLIS, TimeUnit.MILLISECONDS)).isNull();
            }
        }

        @Test
        @DisplayName("registering a regular file throws NotDirectoryException")
        void fileCannotBeRegistered(@TempDir Path tmp) throws Exception {
            Path file = Files.createFile(tmp.resolve("f.txt"));
            try (WatchService watcher = newWatcher()) {
                assertThatThrownBy(() -> file.register(watcher, StandardWatchEventKinds.ENTRY_CREATE))
                        .isInstanceOf(NotDirectoryException.class);
            }
        }
    }

    // ── 3. Key lifecycle ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Key lifecycle")
    class KeyLifecycleTest {

        @Test
        @DisplayName("without reset() the key is not handed out again; reset() redelivers pending events")
        void resetRedelivers(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                WatchKey key = tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

                Files.createFile(tmp.resolve("first.txt"));
                WatchKey firstDelivery = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
                assertThat(firstDelivery).isSameAs(key);
                key.pollEvents();

                Files.createFile(tmp.resolve("second.txt"));
                assertThat(watcher.poll(QUIET_MILLIS, TimeUnit.MILLISECONDS))
                        .as("key was not reset, so it must not be queued again")
                        .isNull();

                assertThat(key.reset()).isTrue();
                WatchKey secondDelivery = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
                assertThat(secondDelivery).isSameAs(key);

                List<Object> contexts = new ArrayList<>();
                for (WatchEvent<?> event : secondDelivery.pollEvents()) {
                    contexts.add(event.context());
                }
                assertThat(contexts).containsExactly(Paths.get("second.txt"));
            }
        }

        @Test
        @DisplayName("cancel() invalidates the key and reset() then returns false")
        void cancel(@TempDir Path tmp) throws Exception {
            try (WatchService watcher = newWatcher()) {
                WatchKey key = tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                assertThat(key.isValid()).isTrue();
                key.cancel();
                assertThat(key.isValid()).isFalse();
                assertThat(key.reset()).isFalse();
            }
        }

        @Test
        @DisplayName("closing the service invalidates its keys")
        void closeInvalidatesKeys(@TempDir Path tmp) throws Exception {
            WatchService watcher = newWatcher();
            WatchKey key = tmp.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            watcher.close();
            assertThat(key.isValid()).isFalse();
        }

        @Test
        @DisplayName("using a closed service throws ClosedWatchServiceException")
        void closedService() throws Exception {
            WatchService watcher = newWatcher();
            watcher.close();
            assertThatThrownBy(watcher::poll).isInstanceOf(ClosedWatchServiceException.class);
        }
    }
}
