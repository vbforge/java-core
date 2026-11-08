package com.vbforge.practice.practice_6_conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** valueToTree -> edit the tree -> treeToValue: change one field of an immutable record the Jackson way. */
public class ValueToTreeAndBackDemo {

    public record Person(String name, int age) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Person original = new Person("Alice", 30);

        JsonNode tree = mapper.valueToTree(original);
        System.out.println("tree      : " + tree);

        ((ObjectNode) tree).put("age", 31);
        Person older = mapper.treeToValue(tree, Person.class); // checked: JsonProcessingException
        System.out.println("edited    : " + older);
        System.out.println("original  : " + original);
    }
}
