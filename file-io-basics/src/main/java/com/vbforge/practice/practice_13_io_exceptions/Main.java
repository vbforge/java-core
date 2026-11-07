package com.vbforge.practice.practice_13_io_exceptions;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        File file = new File("file-io-basics/data_files/ContentHelloWorld.txt");
        ContentReader cr = new ContentReader();

        try {
            String content = cr.readerFile(file);
            System.out.println("content: " + content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("FileNotFoundException");
            throw new RuntimeException(e);
        }
    }
}