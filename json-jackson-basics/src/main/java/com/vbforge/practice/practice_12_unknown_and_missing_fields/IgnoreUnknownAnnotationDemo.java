package com.vbforge.practice.practice_12_unknown_and_missing_fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonIgnoreProperties(ignoreUnknown = true): the per-class way to be tolerant, e.g. for an external API. */
public class IgnoreUnknownAnnotationDemo {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Point(int x, int y) { }

    public static void main(String[] args) throws JsonProcessingException {
        Point point = new ObjectMapper().readValue("{\"x\":1,\"y\":2,\"z\":3,\"label\":\"A\"}", Point.class);
        System.out.println(point);
    }
}
