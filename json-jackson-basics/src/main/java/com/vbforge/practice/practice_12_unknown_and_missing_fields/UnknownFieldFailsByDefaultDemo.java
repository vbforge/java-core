package com.vbforge.practice.practice_12_unknown_and_missing_fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/** By default an unknown JSON field fails the read; the exception tells you which field it was. */
public class UnknownFieldFailsByDefaultDemo {

    public record Point(int x, int y) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"x\":1,\"y\":2,\"z\":3}", Point.class);
        } catch (UnrecognizedPropertyException e) {
            System.out.println("unknown property : " + e.getPropertyName());
            System.out.println("in class         : " + e.getReferringClass().getSimpleName());
        }
    }
}
