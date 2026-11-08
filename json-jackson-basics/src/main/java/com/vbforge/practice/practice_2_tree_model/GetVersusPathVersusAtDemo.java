package com.vbforge.practice.practice_2_tree_model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** get() can return Java null, path() returns a MissingNode you can chain on, at() takes a JSON Pointer. */
public class GetVersusPathVersusAtDemo {

    public static void main(String[] args) throws JsonProcessingException {
        JsonNode root = new ObjectMapper().readTree("{\"user\":{\"address\":{\"city\":\"Kyiv\"}}}");

        System.out.println("get(\"missing\") == null            : " + (root.get("missing") == null));
        try {
            root.get("missing").asText();
        } catch (NullPointerException e) {
            System.out.println("get(\"missing\").asText()           : NullPointerException");
        }

        JsonNode missing = root.path("missing");
        System.out.println("path(\"missing\") is MissingNode    : " + missing.isMissingNode());
        System.out.println("path(\"missing\").asText(\"n/a\")     : " + missing.asText("n/a"));

        System.out.println("path chain city                  : " + root.path("user").path("address").path("city").asText());
        System.out.println("at(\"/user/address/city\")          : " + root.at("/user/address/city").asText());
        System.out.println("at(\"/user/phone/home\") is missing : " + root.at("/user/phone/home").isMissingNode());
    }
}
