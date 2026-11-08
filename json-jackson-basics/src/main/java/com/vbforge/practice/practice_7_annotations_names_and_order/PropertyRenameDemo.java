package com.vbforge.practice.practice_7_annotations_names_and_order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/** @JsonProperty renames in BOTH directions; the Java name stops working on input. */
public class PropertyRenameDemo {

    public static class Product {
        @JsonProperty("product_name")
        private String name;
        @JsonProperty("unit_price")
        private double price;

        public Product() { }

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() { return "Product[name=" + name + ", price=" + price + "]"; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("write: " + mapper.writeValueAsString(new Product("Pen", 1.5)));
        System.out.println("read : " + mapper.readValue("{\"product_name\":\"Pen\",\"unit_price\":1.5}", Product.class));

        try {
            mapper.readValue("{\"name\":\"Pen\",\"price\":1.5}", Product.class);
        } catch (UnrecognizedPropertyException e) {
            System.out.println("old name \"" + e.getPropertyName() + "\" -> UnrecognizedPropertyException");
        }
    }
}
