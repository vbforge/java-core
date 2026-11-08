package com.vbforge.practice.practice_5_records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Records need no annotations (Jackson 2.12+): write and read back an equal, immutable value. */
public class RecordRoundTripDemo {

    public record Person(String name, int age) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Person original = new Person("Alice", 30);

        String json = mapper.writeValueAsString(original);
        Person copy = mapper.readValue(json, Person.class);

        System.out.println("json : " + json);
        System.out.println("copy : " + copy);
        System.out.println("equal: " + original.equals(copy));
    }
}
