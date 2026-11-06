package com.vbforge.practice.practice_8_generic_methods;

/**
 * A static generic factory method vs an instance generic method: in both cases the
 * method has its own type parameter, independent of the class's T, and the compiler
 * infers it from the argument.
 */
public class GenericFactoryMethodsDemo {

    public static void main(String[] args) {
        Box<String> box1 = Box.box("1");               // a box with String
        Box<Integer> box2 = new BoxProvider().box(2);  // a box with Integer
        System.out.println(box1); // Box{value=1}
        System.out.println(box2); // Box{value=2}
    }

    static class Box<T> {
        private final T value;

        public Box(T value) {
            this.value = value;
        }

        // static: no class-level T available, so the method declares its own <V>
        public static <V> Box<V> box(V value) {
            return new Box<>(value);
        }

        @Override
        public String toString() {
            return "Box{" + "value=" + value + '}';
        }
    }

    static class BoxProvider {
        public <T> Box<T> box(T value) {
            return new Box<>(value);
        }
    }
}
