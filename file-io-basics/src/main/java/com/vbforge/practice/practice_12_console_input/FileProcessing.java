package com.vbforge.practice.practice_12_console_input;

import java.io.File;
import java.util.Scanner;

public class FileProcessing {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("file-io-basics/data_files/data.txt"));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Error reading file: " + e);
        }
    }
}