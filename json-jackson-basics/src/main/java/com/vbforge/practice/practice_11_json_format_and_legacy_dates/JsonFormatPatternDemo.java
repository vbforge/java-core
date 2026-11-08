package com.vbforge.practice.practice_11_json_format_and_legacy_dates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

/** @JsonFormat(pattern = ...) controls the text format in BOTH directions, no feature flags needed. */
public class JsonFormatPatternDemo {

    public static class Invoice {
        public String number;
        @JsonFormat(pattern = "dd/MM/yyyy")
        public LocalDate issued;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Invoice invoice = new Invoice();
        invoice.number = "INV-1";
        invoice.issued = LocalDate.of(2024, 1, 15);
        System.out.println("write: " + mapper.writeValueAsString(invoice));

        Invoice read = mapper.readValue("{\"number\":\"INV-2\",\"issued\":\"20/03/2024\"}", Invoice.class);
        System.out.println("read : " + read.number + " issued " + read.issued);
    }
}
