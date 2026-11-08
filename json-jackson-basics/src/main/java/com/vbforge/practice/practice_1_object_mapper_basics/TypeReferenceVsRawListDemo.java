package com.vbforge.practice.practice_1_object_mapper_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/** Generics are erased: List.class loses the element type, new TypeReference<List<T>>() { } keeps it. */
public class TypeReferenceVsRawListDemo {

    public record Item(int id) { }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "[{\"id\":1},{\"id\":2}]";

        List<Item> raw = mapper.readValue(json, List.class); // compiles, but the elements are not Items
        System.out.println("List.class element type      : " + ((Object) raw.get(0)).getClass().getSimpleName());
        try {
            Item first = raw.get(0);
            System.out.println(first);
        } catch (ClassCastException e) {
            System.out.println("using it as Item             : ClassCastException");
        }

        List<Item> typed = mapper.readValue(json, new TypeReference<List<Item>>() { }); // note the trailing {}
        System.out.println("TypeReference element type   : " + typed.get(0).getClass().getSimpleName());
        System.out.println("typed list                   : " + typed);
    }
}
