package com.vbforge.practice.practice_4_pojos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Writing a POJO uses public getters. isActive() becomes "active"; a field without a getter is not written. */
public class PojoWriteDemo {

    public static class User {
        private final String name;
        private final int age;
        private final boolean active;
        private final String internalNote = "never written"; // private, no getter

        public User(String name, int age, boolean active) {
            this.name = name;
            this.age = age;
            this.active = active;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public boolean isActive() { return active; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(new User("Alice", 30, true));
        System.out.println(json);
        System.out.println("contains \"active\"       : " + json.contains("\"active\""));
        System.out.println("contains \"isActive\"     : " + json.contains("isActive"));
        System.out.println("contains internalNote   : " + json.contains("internalNote"));
    }
}
