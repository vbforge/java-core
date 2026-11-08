package com.vbforge.practice.practice_9_temp_dir_file_tests;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Stores each note as {title}.txt (UTF-8) in one directory. The directory is injected, so tests can pass a temp dir. */
public class NoteStore {

    private static final Pattern SAFE_TITLE = Pattern.compile("[A-Za-z0-9_-]{1,50}");
    private static final String EXTENSION = ".txt";

    private final Path directory;

    public NoteStore(Path directory) {
        try {
            this.directory = Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot create " + directory, e);
        }
    }

    public Path save(String title, String text) {
        Path file = fileFor(title);
        try {
            return Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("cannot write " + file, e);
        }
    }

    public String read(String title) {
        Path file = fileFor(title);
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new NoteNotFoundException(title);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + file, e);
        }
    }

    public boolean delete(String title) {
        try {
            return Files.deleteIfExists(fileFor(title));
        } catch (IOException e) {
            throw new UncheckedIOException("cannot delete " + title, e);
        }
    }

    public List<String> titles() {
        try (Stream<Path> files = Files.list(directory)) {
            return files.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(EXTENSION))
                    .map(n -> n.substring(0, n.length() - EXTENSION.length()))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("cannot list " + directory, e);
        }
    }

    /** Whitelist validation: a title like "../secret" must never leave the directory. */
    private Path fileFor(String title) {
        if (title == null || !SAFE_TITLE.matcher(title).matches()) {
            throw new IllegalArgumentException("invalid title: " + title);
        }
        return directory.resolve(title + EXTENSION);
    }
}
