package com.vbforge.practice.practice_3_generic_class_basics;

/**
 * A generic class with ONE type parameter: the setter style (Box) and the
 * constructor + toString style (BoxPrinter). T is chosen per instance.
 */
public class SingleTypeParameterClassDemo {

    public static void main(String[] args) {

        // ================== Scene 1: Box - setter style, explicit type argument ==================
        Box<String> box = new Box<String>();
        box.setValue("Hello");          // box can hold Strings only
        String value = box.getValue();  // no casting needed
        System.out.println(value);

        // ================== Scene 2: BoxPrinter - constructor + toString, diamond ==================
        BoxPrinter<Integer> value1 = new BoxPrinter<>(10);
        System.out.println(value1);

        BoxPrinter<String> value2 = new BoxPrinter<>("Hello World");
        System.out.println(value2);

        BoxPrinter<Double> value3 = new BoxPrinter<>(2.6);
        System.out.println(value3);
    }

    static class Box<T> {
        private T value;

        public void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    static class BoxPrinter<T> {
        private final T value; // type of the argument

        public BoxPrinter(T arg) {
            value = arg;
        }

        @Override
        public String toString() {
            return "[" + value + "]";
        }
    }
}
