package com.vbforge.practice.practice_8_annotations_visibility_and_inclusion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonIgnore hides a property in BOTH directions: not written, and an incoming value is thrown away. */
public class JsonIgnoreBothWaysDemo {

    public static class Account {
        public String name;
        @JsonIgnore
        public String password;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Account account = new Account();
        account.name = "alice";
        account.password = "secret";
        System.out.println("write: " + mapper.writeValueAsString(account));

        Account read = mapper.readValue("{\"name\":\"alice\",\"password\":\"secret\"}", Account.class);
        System.out.println("read : name=" + read.name + ", password=" + read.password);
    }
}
