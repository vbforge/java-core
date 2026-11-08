package com.vbforge.practice.practice_20_static_state_per_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.Counter;
import com.vbforge.practice.practice_5_child_first_loader.ChildFirstClassLoader;

/**
 * A static field belongs to a class, and a class is (name + loader). The same Counter loaded by
 * three loaders keeps three independent counters: static is "one per class", not "one per JVM".
 */
public class StaticStatePerLoaderDemo {

    public static void main(String[] args) throws Exception {
        String name = Counter.class.getName();
        ClassLoader system = ClassLoader.getSystemClassLoader();
        ChildFirstClassLoader loaderA = new ChildFirstClassLoader(system, name);
        ChildFirstClassLoader loaderB = new ChildFirstClassLoader(system, name);

        System.out.println("application loader: " + Counter.next() + ", " + Counter.next());
        System.out.println("loader A:           " + next(loaderA, name));
        System.out.println("loader B:           " + next(loaderB, name) + ", " + next(loaderB, name) + ", " + next(loaderB, name));
        System.out.println("loader A again:     " + next(loaderA, name));
    }

    private static int next(ClassLoader loader, String className) throws Exception {
        return (Integer) loader.loadClass(className).getMethod("next").invoke(null);
    }
}
