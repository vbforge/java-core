package com.vbforge.practice.practice_5_records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** A record component missing from the JSON is NOT an error: it gets the default value (0 / false / null). */
public class MissingComponentDefaultDemo {

    public record Person(String name, int age, boolean admin) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper.readValue("{\"name\":\"Bob\"}", Person.class));
        System.out.println(mapper.readValue("{\"age\":5}", Person.class));
        System.out.println(mapper.readValue("{}", Person.class));
    }
}
