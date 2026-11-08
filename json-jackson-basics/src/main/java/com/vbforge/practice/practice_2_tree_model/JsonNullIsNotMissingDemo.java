package com.vbforge.practice.practice_2_tree_model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** JSON null is a NullNode (a real object), an absent field is a MissingNode; neither is Java null from path(). */
public class JsonNullIsNotMissingDemo {

    public static void main(String[] args) throws JsonProcessingException {
        JsonNode root = new ObjectMapper().readTree("{\"email\":null,\"name\":\"Alice\"}");

        JsonNode email = root.get("email");
        System.out.println("get(\"email\") is Java null  : " + (email == null));
        System.out.println("email.isNull()             : " + email.isNull());
        System.out.println("email node type            : " + email.getNodeType());
        System.out.println("absent field node type     : " + root.path("phone").getNodeType());

        System.out.println("has(\"email\")               : " + root.has("email"));
        System.out.println("hasNonNull(\"email\")        : " + root.hasNonNull("email"));
        System.out.println("has(\"phone\")               : " + root.has("phone"));
        System.out.println("hasNonNull(\"name\")         : " + root.hasNonNull("name"));
    }
}
