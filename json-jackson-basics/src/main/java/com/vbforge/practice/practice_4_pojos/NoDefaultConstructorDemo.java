package com.vbforge.practice.practice_4_pojos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

/** Writing works with getters alone, reading needs a creator: no no-arg constructor -> InvalidDefinitionException. */
public class NoDefaultConstructorDemo {

    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(new Point(1, 2));
        System.out.println("write ok: " + json);

        try {
            mapper.readValue(json, Point.class);
        } catch (InvalidDefinitionException e) {
            System.out.println("read fails: " + e.getClass().getSimpleName());
        }
    }
}
