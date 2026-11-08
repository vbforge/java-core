package com.vbforge.practice.practice_6_serializable_inheritance;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** A subclass of a Serializable class is Serializable too; fields of BOTH levels are written. */
public class SerializableSubclassDemo {

    static class Shape implements Serializable {
        private static final long serialVersionUID = 1L;
        String color = "red";
    }

    static class Circle extends Shape {
        private static final long serialVersionUID = 1L;
        int radius = 5;
    }

    public static void main(String[] args) throws Exception {
        Circle c = new Circle();
        c.color = "blue";
        c.radius = 9;

        Circle copy = SerializationHelper.roundTrip(c);
        System.out.println("copy: color=" + copy.color + ", radius=" + copy.radius);
        System.out.println("Circle is Serializable without declaring it: " + (c instanceof Serializable));
    }
}
