package com.vbforge.practice.practice_11_json_format_and_legacy_dates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

/** Pattern letters are case-sensitive: MM = month, mm = MINUTE. The wrong one compiles and silently writes nonsense. */
public class CaseSensitivePatternDemo {

    public static class Good {
        @JsonFormat(pattern = "dd/MM/yyyy")
        public LocalDateTime when = LocalDateTime.of(2024, 1, 15, 10, 30);
    }

    public static class Bad {
        @JsonFormat(pattern = "dd/mm/yyyy")
        public LocalDateTime when = LocalDateTime.of(2024, 1, 15, 10, 30);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        System.out.println("dd/MM/yyyy: " + mapper.writeValueAsString(new Good()));
        System.out.println("dd/mm/yyyy: " + mapper.writeValueAsString(new Bad()) + "   <- 30 is the minute, not a month");
    }
}
