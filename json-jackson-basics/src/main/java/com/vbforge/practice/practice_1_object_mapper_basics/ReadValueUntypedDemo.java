package com.vbforge.practice.practice_1_object_mapper_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/** readValue into Map: what Java type does each JSON value become? (Integer / Long / Double, not just "number") */
public class ReadValueUntypedDemo {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"small\":1,\"big\":3000000000,\"price\":1.5,\"name\":\"x\","
                + "\"flag\":true,\"list\":[1,2],\"nothing\":null}";

        Map<String, Object> map = mapper.readValue(json, Map.class);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String type = value == null ? "null" : value.getClass().getSimpleName();
            System.out.println(entry.getKey() + " -> " + type);
        }
    }
}
