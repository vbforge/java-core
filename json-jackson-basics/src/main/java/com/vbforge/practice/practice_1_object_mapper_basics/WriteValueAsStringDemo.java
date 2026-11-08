package com.vbforge.practice.practice_1_object_mapper_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** writeValueAsString: maps, lists, strings, numbers and null all become JSON text. */
public class WriteValueAsStringDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = new LinkedHashMap<>(); // LinkedHashMap keeps the key order stable
        map.put("name", "Alice");
        map.put("age", 30);
        map.put("admin", false);
        map.put("tags", List.of("a", "b"));
        map.put("nickname", null);

        System.out.println("map    : " + mapper.writeValueAsString(map));
        System.out.println("list   : " + mapper.writeValueAsString(List.of(1, 2, 3)));
        System.out.println("string : " + mapper.writeValueAsString("hi")); // quotes are part of the JSON
        System.out.println("number : " + mapper.writeValueAsString(42));
        System.out.println("null   : " + mapper.writeValueAsString(null));
    }
}
