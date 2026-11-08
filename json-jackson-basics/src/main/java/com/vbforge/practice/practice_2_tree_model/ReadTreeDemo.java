package com.vbforge.practice.practice_2_tree_model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

/** readTree gives a JsonNode tree; every node knows its own JSON type. */
public class ReadTreeDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(
                "{\"user\":{\"name\":\"Alice\",\"age\":30,\"langs\":[\"java\",\"kotlin\"]},\"active\":true}");

        System.out.println("root type       : " + root.getNodeType());
        System.out.println("user type       : " + root.get("user").getNodeType());
        System.out.println("name            : " + root.get("user").get("name").asText() + " (" + root.get("user").get("name").getNodeType() + ")");
        System.out.println("age             : " + root.get("user").get("age").asInt() + " (" + root.get("user").get("age").getNodeType() + ")");
        System.out.println("active          : " + root.get("active").asBoolean() + " (" + root.get("active").getNodeType() + ")");

        JsonNode langs = root.get("user").get("langs");
        System.out.println("langs           : size=" + langs.size() + ", type=" + langs.getNodeType());
        for (JsonNode lang : langs) {
            System.out.println("  lang " + lang.asText());
        }

        Iterator<String> names = root.fieldNames();
        StringBuilder fields = new StringBuilder();
        while (names.hasNext()) {
            fields.append(names.next()).append(' ');
        }
        System.out.println("top-level fields: " + fields.toString().trim());
    }
}
