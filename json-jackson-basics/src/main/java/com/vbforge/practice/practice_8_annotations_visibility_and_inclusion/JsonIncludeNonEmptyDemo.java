package com.vbforge.practice.practice_8_annotations_visibility_and_inclusion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/** NON_EMPTY is stricter than NON_NULL: it also drops "", empty lists and empty maps. */
public class JsonIncludeNonEmptyDemo {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NonNull {
        public String name = "Alice";
        public String nickname = "";
        public List<String> tags = List.of();
        public String email = null;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NonEmpty {
        public String name = "Alice";
        public String nickname = "";
        public List<String> tags = List.of();
        public String email = null;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("NON_NULL : " + mapper.writeValueAsString(new NonNull()));
        System.out.println("NON_EMPTY: " + mapper.writeValueAsString(new NonEmpty()));
    }
}
