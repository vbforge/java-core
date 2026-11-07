package com.vbforge.practice.practice_13_io_exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ContentReaderHelloWorld {
    public static void main(String[] args) {
        try {
            File file = new File("file-io-basics/data_files/ContentHelloWorld.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                System.out.println(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}