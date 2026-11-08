package com.vbforge.practice.practice_4_find_class_and_define_class;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;

/**
 * defineClass turns bytes into a Class owned by the loader that called it.
 * The result has the same name as Greeter but is a different class.
 * A second loadClass finds it through findLoadedClass and defines nothing new.
 */
public class DefineClassFromBytesDemo {

    public static void main(String[] args) throws Exception {
        IsolatingClassLoader loader = new IsolatingClassLoader();
        String name = Greeter.class.getName();

        Class<?> isolated = loader.loadClass(name);
        System.out.println("same name as Greeter:           " + isolated.getName().equals(name));
        System.out.println("defining loader is our loader:  " + (isolated.getClassLoader() == loader));
        System.out.println("same Class as Greeter.class:    " + (isolated == Greeter.class));

        Object instance = isolated.getDeclaredConstructor().newInstance();
        System.out.println("greet() through reflection:     " + isolated.getMethod("greet").invoke(instance));

        System.out.println("classes defined so far:         " + loader.definedCount() + " (Greeter + its interface Speaker)");
        Class<?> again = loader.loadClass(name);
        System.out.println("second loadClass is the same Class: " + (again == isolated));
        System.out.println("classes defined after it:       " + loader.definedCount());
    }
}
