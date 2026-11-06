package com.vbforge.practice.practice_5_generic_array_backed_list;

import com.vbforge.practice.practice_5_generic_array_backed_list.MyListGenericImpl;

/**
 * Using the generic list: creation variants (explicit type argument, diamond,
 * interface vs class reference) and type safety per parameterization.
 */
public class MyListGenericDemo {

    public static void main(String[] args) {

        // ================== Scene 1: object creation variants ==================
        MyListGeneric<Integer> generic1 = new MyListGenericImpl<Integer>();   // explicit type argument
        generic1.add(10);
        //generic1.add("aaa");                                                // compile error

        MyListGenericImpl<String> generic2 = new MyListGenericImpl<String>(); // class-typed reference
        MyListGenericImpl<String> generic3 = new MyListGenericImpl<>();       // diamond (Java 7+): inferred from the left side
        generic2.add("explicit");
        generic3.add("diamond");
        // new MyListGenericImpl() without <> is a RAW type: compiles with an unchecked warning
        // and switches the compiler's protection off (see practice_1_raw_type_class_cast_exception)

        System.out.println(generic1 + " " + generic2 + " " + generic3);

        // ================== Scene 2: type safety per parameterization ==================
        MyListGeneric<Integer> ints = new MyListGenericImpl<>();
        ints.add(10);
        //ints.add("aaa");   // compile error

        MyListGeneric<String> strings = new MyListGenericImpl<>();
        //strings.add(10);   // compile error
        strings.add("aaa");

        // Object parameterization - accepts anything, still checked by the compiler
        MyListGeneric<Object> objects = new MyListGenericImpl<>();
        objects.add(10);
        objects.add("aaa");

        System.out.println("ints = " + ints + ", strings = " + strings + ", objects = " + objects);
    }
}
