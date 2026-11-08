package com.vbforge.practice.practice_10_java_time_module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Instant carries a "Z" (a point on the timeline); LocalDateTime has no zone at all. Both are ISO-8601 strings. */
public class InstantVersusLocalDateTimeDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        LocalDateTime local = LocalDateTime.of(2024, 1, 15, 10, 30);

        System.out.println("Instant        : " + mapper.writeValueAsString(instant));
        System.out.println("LocalDateTime  : " + mapper.writeValueAsString(local));

        Instant readInstant = mapper.readValue("\"2024-01-15T10:30:00Z\"", Instant.class);
        LocalDate readDate = mapper.readValue("\"2024-01-15\"", LocalDate.class);
        System.out.println("read Instant   : " + readInstant + " (equal: " + readInstant.equals(instant) + ")");
        System.out.println("read LocalDate : " + readDate);
    }
}
