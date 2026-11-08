package com.vbforge.practice.practice_13_any_setter_and_any_getter;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** Any-setter + any-getter together: fields the class does not know survive read -> write unchanged. */
public class UnknownFieldsRoundTripDemo {

    public static class Envelope {
        public String type;
        private final Map<String, Object> extras = new LinkedHashMap<>();

        @JsonAnySetter
        public void putExtra(String key, Object value) {
            extras.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> extras() {
            return extras;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String original = "{\"type\":\"click\",\"x\":10,\"tags\":[\"a\",\"b\"],\"meta\":{\"k\":1}}";

        Envelope envelope = mapper.readValue(original, Envelope.class);
        String written = mapper.writeValueAsString(envelope);

        System.out.println("original : " + original);
        System.out.println("written  : " + written);
        System.out.println("same JSON: " + mapper.readTree(original).equals(mapper.readTree(written)));
    }
}
