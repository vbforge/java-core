package com.vbforge.practice.practice_10_java_time_module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

/** Reading a LocalDate expects ISO "yyyy-MM-dd"; any other text fails with a JsonMappingException. */
public class BadDateInputDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        for (String text : new String[] {"2024-01-15", "15-01-2024", "2024-02-30"}) {
            try {
                LocalDate date = mapper.readValue("\"" + text + "\"", LocalDate.class);
                System.out.println(text + " -> " + date);
            } catch (JsonMappingException e) {
                System.out.println(text + " -> JsonMappingException");
            }
        }
    }
}
