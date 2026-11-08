package com.vbforge.practice.practice_11_json_format_and_legacy_dates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

/** java.util.Date works without any module: written as epoch millis by default, read from millis or ISO text. */
public class LegacyDateDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        long millis = 1_705_314_600_000L; // 2024-01-15T10:30:00Z

        System.out.println("write Date      : " + mapper.writeValueAsString(new Date(millis)));
        System.out.println("read from millis: " + (mapper.readValue("1705314600000", Date.class).getTime() == millis));
        System.out.println("read from ISO   : " + (mapper.readValue("\"2024-01-15T10:30:00.000Z\"", Date.class).getTime() == millis));
    }
}
