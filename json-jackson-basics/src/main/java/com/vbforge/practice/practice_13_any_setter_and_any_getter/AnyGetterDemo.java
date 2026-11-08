package com.vbforge.practice.practice_13_any_setter_and_any_getter;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** @JsonAnyGetter writes the Map entries as top-level fields, flat, not nested under "extras". */
public class AnyGetterDemo {

    public static class Event {
        public String type = "click";
        private final Map<String, Object> extras = new LinkedHashMap<>();

        public Event() {
            extras.put("x", 10);
            extras.put("y", 20);
        }

        @JsonAnyGetter
        public Map<String, Object> extras() {
            return extras;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(new Event()));
    }
}
