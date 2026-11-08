package com.vbforge.practice.practice_13_any_setter_and_any_getter;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** @JsonAnySetter collects every JSON key the class does not know into a Map instead of failing. */
public class AnySetterDemo {

    public static class Event {
        public String type;
        public final Map<String, Object> extras = new LinkedHashMap<>();

        @JsonAnySetter
        public void putExtra(String key, Object value) {
            extras.put(key, value);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        Event event = new ObjectMapper().readValue(
                "{\"type\":\"click\",\"x\":10,\"y\":20,\"meta\":{\"browser\":\"fx\"}}", Event.class);

        System.out.println("type   : " + event.type);
        System.out.println("extras : " + event.extras);
    }
}
