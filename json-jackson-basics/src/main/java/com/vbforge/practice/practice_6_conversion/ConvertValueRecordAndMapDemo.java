package com.vbforge.practice.practice_6_conversion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** convertValue: object <-> Map in memory, no JSON text in between. */
public class ConvertValueRecordAndMapDemo {

    public record Person(String name, int age) { }

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = mapper.convertValue(new Person("Alice", 30), new TypeReference<Map<String, Object>>() { });
        System.out.println("record -> map   : " + map);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("name", "Bob");
        input.put("age", 25);
        Person person = mapper.convertValue(input, Person.class);
        System.out.println("map    -> record: " + person);
    }
}
