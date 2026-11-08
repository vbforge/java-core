package com.vbforge.practice.practice_6_conversion;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/** convertValue reports failures as an unchecked IllegalArgumentException, with the Jackson exception as the cause. */
public class ConvertValueFailureDemo {

    public record Person(String name, int age) { }

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.convertValue(Map.of("name", "Bob", "age", "abc"), Person.class);
        } catch (IllegalArgumentException e) {
            System.out.println("bad value    -> IllegalArgumentException, cause is JsonMappingException: "
                    + (e.getCause() instanceof JsonMappingException));
        }

        try {
            mapper.convertValue(Map.of("name", "Bob", "age", 1, "extra", true), Person.class);
        } catch (IllegalArgumentException e) {
            System.out.println("unknown field-> IllegalArgumentException, cause is JsonMappingException: "
                    + (e.getCause() instanceof JsonMappingException));
        }
    }
}
