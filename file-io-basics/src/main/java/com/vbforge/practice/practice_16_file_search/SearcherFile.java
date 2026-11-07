package com.vbforge.practice.practice_16_file_search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearcherFile {

    public List<File> searchFiles(String directory, String searchCriteria) {
        List<File> resultList = new ArrayList<>();
        File file = new File(directory);

        if (!file.isDirectory()) {
            return resultList;
        }

        File[] files = file.listFiles();
        if (files == null) {
            return resultList;
        }

        for (File item : files) {
            if (item.getName().endsWith(searchCriteria)) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}