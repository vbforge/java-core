package com.vbforge.practice.practice_10_java_time_module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

/** java.time needs JavaTimeModule, and even then dates are numbers until WRITE_DATES_AS_TIMESTAMPS is disabled. */
public class DateMapperSetupDemo {

    public static void main(String[] args) throws JsonProcessingException {
        LocalDate date = LocalDate.of(2024, 1, 15);

        try {
            new ObjectMapper().writeValueAsString(date);
        } catch (InvalidDefinitionException e) {
            System.out.println("plain mapper        : InvalidDefinitionException");
        }

        ObjectMapper withModule = new ObjectMapper().registerModule(new JavaTimeModule());
        System.out.println("module only         : " + withModule.writeValueAsString(date));

        ObjectMapper iso = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println("module + no stamps  : " + iso.writeValueAsString(date));
    }
}
