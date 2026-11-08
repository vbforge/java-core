package com.vbforge.practice.practice_7_annotations_names_and_order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonPropertyOrder fixes the order of the fields in the output: explicit list or alphabetic. */
public class PropertyOrderDemo {

    @JsonPropertyOrder({"id", "name", "email"})
    public static class Explicit {
        public String email = "a@b.c";
        public String name = "Alice";
        public int id = 1;
    }

    @JsonPropertyOrder(alphabetic = true)
    public static class Alphabetic {
        public String zip = "01001";
        public String city = "Kyiv";
        public String street = "Main 1";
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("explicit  : " + mapper.writeValueAsString(new Explicit()));
        System.out.println("alphabetic: " + mapper.writeValueAsString(new Alphabetic()));
    }
}
