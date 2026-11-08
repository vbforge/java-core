package com.vbforge.practice.practice_5_records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/** A JSON array of objects <-> List of records, with TypeReference on the way in. */
public class RecordListDemo {

    public record Person(String name, int age) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<Person> people = mapper.readValue(
                "[{\"name\":\"Alice\",\"age\":30},{\"name\":\"Bob\",\"age\":25}]",
                new TypeReference<List<Person>>() { });

        for (Person person : people) {
            System.out.println(person);
        }
        System.out.println("back to JSON: " + mapper.writeValueAsString(people));
    }
}
