package com.vbforge.practice.practice_12_unknown_and_missing_fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Disabling FAIL_ON_UNKNOWN_PROPERTIES on the whole mapper: a typo in a field name now fails silently. */
public class GlobalDisableHidesTyposDemo {

    public static class Customer {
        public String name;
        public String email;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Customer customer = mapper.readValue("{\"nmae\":\"Alice\",\"email\":\"a@b.c\"}", Customer.class); // "nmae" is a typo
        System.out.println("name  = " + customer.name + "   <- the typo vanished without an error");
        System.out.println("email = " + customer.email);
    }
}
