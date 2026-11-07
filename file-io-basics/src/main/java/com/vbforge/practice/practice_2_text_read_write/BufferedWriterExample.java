package com.vbforge.practice.practice_2_text_read_write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedWriterExample {
    public static void main(String[] args) {
        String content =
            "Commonly Used Classes:\n" +
            "* File: Represents file and directory pathnames.\n" +
            "* FileInputStream: Reads raw bytes from a file.\n" +
            "* FileOutputStream: Writes raw bytes to a file.\n" +
            "* FileReaderDemo: Reads characters from a file.\n" +
            "* FileWriter: Writes characters to a file.\n" +
            "* BufferedReader: Reads text from an input stream, buffering characters to provide efficient reading.\n" +
            "* BufferedWriter: Writes text to an output stream, buffering characters for efficient writing.\n";
        File file = new File("file-io-basics/data_files/BufferedWriterExample.txt");
        write(content, file);
        System.out.println(content);
    }

    private static void write(String content, File file) {
        if (content.length() == 0 || content.length() > 1024 || content.trim().length() == 0) {
            throw new RuntimeException("Content is empty or too long (> 1024 bytes)");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}