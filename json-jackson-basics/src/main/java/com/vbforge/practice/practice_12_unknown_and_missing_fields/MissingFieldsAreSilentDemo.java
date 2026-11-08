package com.vbforge.practice.practice_12_unknown_and_missing_fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Missing fields do NOT fail by default (a silent 0 can be a nasty bug).
 * FAIL_ON_MISSING_CREATOR_PROPERTIES reports them, but only for records / constructor creators, not for setter POJOs.
 */
public class MissingFieldsAreSilentDemo {

    public record Point(int x, int y) { }

    public static class SetterPoint {
        public int x;
        public int y;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("record, default      : " + mapper.readValue("{}", Point.class));

        ObjectMapper strict = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        try {
            strict.readValue("{\"x\":1}", Point.class);
        } catch (JsonMappingException e) {
            System.out.println("record, feature on   : JsonMappingException (y is missing)");
        }

        SetterPoint pojo = strict.readValue("{\"x\":1}", SetterPoint.class);
        System.out.println("setter POJO, feature on: no error, y=" + pojo.y);
    }
}
