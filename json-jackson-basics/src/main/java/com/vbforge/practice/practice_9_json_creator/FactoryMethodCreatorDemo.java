package com.vbforge.practice.practice_9_json_creator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonCreator on a static factory: the validation lives in the factory; its exception is wrapped by Jackson. */
public class FactoryMethodCreatorDemo {

    public static final class Percentage {
        private final int value;

        private Percentage(int value) {
            this.value = value;
        }

        @JsonCreator
        public static Percentage of(@JsonProperty("value") int value) {
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("percentage out of range: " + value);
            }
            return new Percentage(value);
        }

        public int getValue() { return value; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Percentage ok = mapper.readValue("{\"value\":42}", Percentage.class);
        System.out.println("valid  : " + ok.getValue());

        try {
            mapper.readValue("{\"value\":142}", Percentage.class);
        } catch (JsonMappingException e) {
            System.out.println("invalid: JsonMappingException, cause is IllegalArgumentException: "
                    + (e.getCause() instanceof IllegalArgumentException));
        }
    }
}
