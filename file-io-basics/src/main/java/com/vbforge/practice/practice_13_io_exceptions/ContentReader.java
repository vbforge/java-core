package com.vbforge.practice.practice_13_io_exceptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ContentReader {

    public String readerFile(File f) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(f);

        return "text";
    }
}