package com.vbforge.practice.practice_12_unknown_and_missing_fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/** Loosen at the narrowest scope: one reader ignores unknown fields, the shared mapper stays strict. */
public class LenientReaderDemo {

    public record Point(int x, int y) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"x\":1,\"y\":2,\"z\":3}";

        Point lenient = mapper.readerFor(Point.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(json);
        System.out.println("lenient reader : " + lenient);

        try {
            mapper.readValue(json, Point.class);
        } catch (UnrecognizedPropertyException e) {
            System.out.println("shared mapper  : still strict (UnrecognizedPropertyException)");
        }
    }
}
