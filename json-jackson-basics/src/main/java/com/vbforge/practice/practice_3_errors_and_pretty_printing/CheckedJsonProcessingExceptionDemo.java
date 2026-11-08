package com.vbforge.practice.practice_3_errors_and_pretty_printing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/** JsonProcessingException is CHECKED (it extends IOException): catch it or declare it. */
public class CheckedJsonProcessingExceptionDemo {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValueAsString(new Object()); // a bean without any properties cannot be serialized
        } catch (JsonProcessingException e) {
            System.out.println("caught as JsonProcessingException: " + e.getClass().getSimpleName());
            System.out.println("is an IOException               : " + (e instanceof IOException));
        }
    }
}
