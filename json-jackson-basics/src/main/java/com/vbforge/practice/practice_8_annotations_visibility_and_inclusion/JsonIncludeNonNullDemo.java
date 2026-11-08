package com.vbforge.practice.practice_8_annotations_visibility_and_inclusion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonInclude(NON_NULL): null fields are left out of the output. Default is to write them as null. */
public class JsonIncludeNonNullDemo {

    public static class Always {
        public String name = "Alice";
        public String email = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NonNull {
        public String name = "Alice";
        public String email = null;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("default : " + mapper.writeValueAsString(new Always()));
        System.out.println("NON_NULL: " + mapper.writeValueAsString(new NonNull()));
    }
}
