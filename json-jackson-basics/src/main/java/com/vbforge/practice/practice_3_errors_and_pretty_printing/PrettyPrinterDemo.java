package com.vbforge.practice.practice_3_errors_and_pretty_printing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** writerWithDefaultPrettyPrinter: same data, indented. The mapper itself stays compact. */
public class PrettyPrinterDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "Alice");
        data.put("tags", List.of("a", "b"));
        data.put("address", Map.of("city", "Kyiv"));

        System.out.println("compact:");
        System.out.println(mapper.writeValueAsString(data));

        System.out.println("pretty:");
        String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        pretty.lines().forEach(System.out::println); // lines() hides the platform line separator

        System.out.println("mapper still compact: " + mapper.writeValueAsString(data).contains("\n"));
    }
}
