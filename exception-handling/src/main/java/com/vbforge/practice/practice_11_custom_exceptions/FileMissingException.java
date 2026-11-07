package com.vbforge.practice.practice_11_custom_exceptions;

/** A checked custom exception for "the file we expected isn't there". */
public class FileMissingException extends Exception {

    public FileMissingException(String message) {
        super(message);
    }

    public FileMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
