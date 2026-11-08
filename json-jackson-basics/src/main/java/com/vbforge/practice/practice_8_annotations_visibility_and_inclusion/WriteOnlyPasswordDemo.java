package com.vbforge.practice.practice_8_annotations_visibility_and_inclusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** access = WRITE_ONLY: the value can be READ from a login request but is never WRITTEN to a response. */
public class WriteOnlyPasswordDemo {

    public static class Credentials {
        public String username;
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        public String password;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Credentials read = mapper.readValue("{\"username\":\"alice\",\"password\":\"secret\"}", Credentials.class);
        System.out.println("read : username=" + read.username + ", password=" + read.password);
        System.out.println("write: " + mapper.writeValueAsString(read));
    }
}
