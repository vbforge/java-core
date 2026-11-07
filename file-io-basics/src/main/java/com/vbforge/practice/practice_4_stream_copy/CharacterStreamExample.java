package com.vbforge.practice.practice_4_stream_copy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Character streams are used for handling textual data. Here's an example using FileReaderDemo and FileWriter
 * */
public class CharacterStreamExample {
    public static void main(String[] args) {
        String inputFile  = "file-io-basics/data_files/input.txt";
        String outputFile = "file-io-basics/data_files/output.txt";
        File input  = new File(inputFile);
        File output = new File(outputFile);

        if (!input.exists()) {
            try {
                input.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!output.exists()) {
            try{
                output.createNewFile();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }

        try (FileReader fr = new FileReader(inputFile);
             FileWriter fw = new FileWriter(outputFile)) {

            int content;
            while ((content = fr.read()) != -1) {
                fw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}