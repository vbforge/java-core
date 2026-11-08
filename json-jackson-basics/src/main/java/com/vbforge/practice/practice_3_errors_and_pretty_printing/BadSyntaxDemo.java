package com.vbforge.practice.practice_3_errors_and_pretty_printing;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/** Broken syntax -> JsonParseException; valid JSON that does not fit the target type -> JsonMappingException. */
public class BadSyntaxDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readTree("{\"a\": }");
        } catch (JsonParseException e) {
            System.out.println("bad syntax        -> JsonParseException");
        }

        try {
            mapper.readValue("[1,2,3]", Map.class);
        } catch (JsonMappingException e) {
            System.out.println("array into a Map  -> JsonMappingException");
        }

        try {
            mapper.readValue("", Map.class);
        } catch (JsonMappingException e) {
            System.out.println("empty input       -> JsonMappingException");
        }

        System.out.println("readTree(\"\") is MissingNode: " + mapper.readTree("").isMissingNode());
    }
}
