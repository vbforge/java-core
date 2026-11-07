package com.vbforge.practice.practice_2_text_read_write;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BufferedReaderExample {
    public static void main(String[] args) {

        File file = new File("file-io-basics/data_files/BufferedReaderExample.txt");
        String content = read(file);
        System.out.println(content);

    }

    private static String read(File file){
        if (!file.exists()){
            throw new RuntimeException("File does not exist");
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return content.toString();
    }

}