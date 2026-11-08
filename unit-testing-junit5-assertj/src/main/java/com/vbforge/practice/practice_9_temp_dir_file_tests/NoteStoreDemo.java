package com.vbforge.practice.practice_9_temp_dir_file_tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NoteStoreDemo {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("notes-demo");
        NoteStore store = new NoteStore(dir);
        store.save("todo", "buy milk");
        System.out.println(store.titles() + " -> " + store.read("todo"));
        store.delete("todo");
        Files.delete(dir);
    }
}
