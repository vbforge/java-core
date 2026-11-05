package com.vbforge.practice.practice_2_tostring_in_string_concat;

/** String concatenation and println(Object) call the object's toString(); an overridden toString controls the text. */
public class ToStringInConcatenationDemo {

    public static void main(String[] args) {
        Box box = new Box(10, 20, 30);
        System.out.println("Attention! " + box);
        System.out.println(box);
        Student john = new Student("John Smith");
        System.out.println("First student: " + john);
    }

    static class Box {
        private final double width, height, depth;

        Box(double w, double h, double d) {
            width = w;
            height = h;
            depth = d;
        }

        @Override
        public String toString() {
            return "Volume: " + width + "/" + height + "/" + depth;
        }
    }

    static class Student {
        private final String name;

        Student(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student [name=" + name + "]";
        }
    }
}
