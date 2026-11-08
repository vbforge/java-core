package com.vbforge.practice.practice_4_pojos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Reading a POJO: Jackson calls the no-arg constructor, then one setter per JSON field. */
public class PojoReadDemo {

    public static class User {
        private String name;
        private int age;
        private boolean active;

        public User() {
            System.out.println("  no-arg constructor called");
        }

        public void setName(String name) { System.out.println("  setName(" + name + ")"); this.name = name; }
        public void setAge(int age) { System.out.println("  setAge(" + age + ")"); this.age = age; }
        public void setActive(boolean active) { System.out.println("  setActive(" + active + ")"); this.active = active; }

        @Override
        public String toString() {
            return "User[name=" + name + ", age=" + age + ", active=" + active + "]";
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println("reading:");
        User user = new ObjectMapper().readValue("{\"name\":\"Bob\",\"age\":25,\"active\":true}", User.class);
        System.out.println("result: " + user);
    }
}
