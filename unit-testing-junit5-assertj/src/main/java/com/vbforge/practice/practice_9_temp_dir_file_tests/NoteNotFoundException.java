package com.vbforge.practice.practice_9_temp_dir_file_tests;

public class NoteNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoteNotFoundException(String title) {
        super("note not found: " + title);
    }
}
