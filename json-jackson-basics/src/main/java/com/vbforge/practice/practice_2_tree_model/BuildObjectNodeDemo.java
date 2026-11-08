package com.vbforge.practice.practice_2_tree_model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Build JSON by hand with ObjectNode / ArrayNode, then edit a parsed tree in place. */
public class BuildObjectNodeDemo {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();
        node.put("id", 1);
        node.put("name", "Alice");
        node.putNull("email");
        ArrayNode tags = node.putArray("tags");
        tags.add("a").add("b");
        node.putObject("address").put("city", "Kyiv");

        System.out.println("built  : " + node);

        JsonNode parsed = mapper.readTree("{\"id\":7,\"old\":true}");
        ((ObjectNode) parsed).put("id", 8);   // overwrite
        ((ObjectNode) parsed).remove("old");  // delete
        ((ObjectNode) parsed).put("seen", true);
        System.out.println("edited : " + parsed);
    }
}
