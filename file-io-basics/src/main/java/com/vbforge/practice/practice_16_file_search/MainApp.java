package com.vbforge.practice.practice_16_file_search;

import java.io.File;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        String directory  = "file-io-basics/data_files";
        String searchCriteria = ".txt";

        SearcherFile searcherFile = new SearcherFile();
        List<File> files = searcherFile.searchFiles(directory, searchCriteria);

        for (File item : files) {
            System.out.println(item);
        }
        System.out.println("list size: " + files.size());
    }
}