package com.vbforge.practice.practice_2_text_read_write;

import java.io.FileReader;
import java.io.IOException;

public class FileReaderDemo {
    public static void main(String[] args) {

        try (FileReader f = new FileReader("/a.txt")) {
            System.out.print("Try block ");
        } catch (IOException e) {
            System.out.print("Catch block ");
        }
//        System.out.print(f.read());
    }
}