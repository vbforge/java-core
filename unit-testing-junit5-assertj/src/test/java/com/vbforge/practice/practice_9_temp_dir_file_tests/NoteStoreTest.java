package com.vbforge.practice.practice_9_temp_dir_file_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Technique: file-system tests with @TempDir.
 * JUnit creates a fresh directory per test (for a non-static field) and deletes it afterwards,
 * so tests never touch real folders, never collide with each other and leave nothing behind.
 */
class NoteStoreTest {

    @TempDir
    Path dir;

    private NoteStore store;

    @BeforeEach
    void setUp() {
        store = new NoteStore(dir);
    }

    @Test
    void save_createsFileWithExactContent() {
        Path file = store.save("todo", "buy milk");

        assertThat(file).exists().isRegularFile().hasFileName("todo.txt").hasContent("buy milk");
    }

    @Test
    void save_existingTitle_overwrites() {
        store.save("todo", "old");

        store.save("todo", "new");

        assertThat(store.read("todo")).isEqualTo("new");
        assertThat(store.titles()).containsExactly("todo");
    }

    @Test
    void read_roundTripKeepsNonAsciiText() {
        String text = "Привіт, світе! \u00e9\u00e8 \u20ac";   // UTF-8 must survive, not the platform charset

        store.save("greeting", text);

        assertThat(store.read("greeting")).isEqualTo(text);
    }

    @Test
    void read_missingNote_throwsDomainException() {
        assertThatThrownBy(() -> store.read("ghost"))
                .isInstanceOf(NoteNotFoundException.class)
                .hasMessage("note not found: ghost");
    }

    @Test
    void titles_areSortedAndIgnoreForeignFiles() throws IOException {
        store.save("b-note", "2");
        store.save("a-note", "1");
        Files.writeString(dir.resolve("readme.md"), "not a note", StandardCharsets.UTF_8);

        assertThat(store.titles()).containsExactly("a-note", "b-note");
    }

    @Test
    void delete_existingNote_removesFileAndReturnsTrue() {
        Path file = store.save("todo", "x");

        boolean deleted = store.delete("todo");

        assertThat(deleted).isTrue();
        assertThat(file).doesNotExist();
    }

    @Test
    void delete_missingNote_returnsFalse() {
        assertThat(store.delete("ghost")).isFalse();
    }

    @Test
    void constructor_createsMissingDirectory(@TempDir Path root) {   // @TempDir also works as a parameter
        Path nested = root.resolve("a/b/c");

        new NoteStore(nested);

        assertThat(nested).isDirectory();
    }

    // ----- security: path traversal -----

    @ParameterizedTest(name = "title \"{0}\" is rejected")
    @ValueSource(strings = {"../secret", "..\\secret", "a/b", "", " ", "name.txt", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})   // last one: 51 chars, limit is 50
    void invalidTitles_areRejected(String title) {
        assertThatThrownBy(() -> store.save(title, "x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.read(title)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    void nullTitle_isRejected(String title) {
        assertThatThrownBy(() -> store.save(title, "x")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectedTitle_writesNothingOutsideTheDirectory() throws IOException {
        Path parent = dir.getParent();
        long before = Files.list(parent).count();

        assertThatThrownBy(() -> store.save("../escaped", "x")).isInstanceOf(IllegalArgumentException.class);

        assertThat(Files.list(parent).count()).isEqualTo(before);
    }

    // ----- I/O failures become unchecked exceptions with the cause attached -----

    @Test
    void constructor_whenPathIsAnExistingFile_throwsUncheckedIoException() throws IOException {
        Path file = Files.writeString(dir.resolve("not-a-dir"), "x");

        assertThatThrownBy(() -> new NoteStore(file))
                .isInstanceOf(java.io.UncheckedIOException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void read_whenNoteIsActuallyADirectory_throwsUncheckedIoException() throws IOException {
        Files.createDirectory(dir.resolve("broken.txt"));

        assertThatThrownBy(() -> store.read("broken"))
                .isInstanceOf(java.io.UncheckedIOException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void save_whenTargetIsADirectory_throwsUncheckedIoException() throws IOException {
        Files.createDirectory(dir.resolve("taken.txt"));

        assertThatThrownBy(() -> store.save("taken", "x"))
                .isInstanceOf(java.io.UncheckedIOException.class);
    }

    @Test
    void delete_whenTargetIsANonEmptyDirectory_throwsUncheckedIoException() throws IOException {
        Path folder = Files.createDirectory(dir.resolve("full.txt"));
        Files.writeString(folder.resolve("inner"), "x");

        assertThatThrownBy(() -> store.delete("full"))
                .isInstanceOf(java.io.UncheckedIOException.class);
    }

    @Test
    void titles_whenDirectoryWasRemoved_throwsUncheckedIoException() throws IOException {
        Path vanished = dir.resolve("vanishing");
        NoteStore other = new NoteStore(vanished);
        Files.delete(vanished);

        assertThatThrownBy(other::titles).isInstanceOf(java.io.UncheckedIOException.class);
    }
}
