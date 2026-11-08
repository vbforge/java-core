package com.vbforge.practice.practice_6_class_identity_name_plus_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_5_child_first_loader.ChildFirstClassLoader;

/**
 * A runtime type is (fully qualified name + defining loader). Three loaders give
 * three different Greeter classes with one name. Comparing names says "same",
 * comparing the Class objects tells the truth.
 */
public class ClassIdentityNamePlusLoaderDemo {

    public static void main(String[] args) throws Exception {
        String name = Greeter.class.getName();
        ClassLoader system = ClassLoader.getSystemClassLoader();
        ChildFirstClassLoader loaderA = new ChildFirstClassLoader(system, name);
        ChildFirstClassLoader loaderB = new ChildFirstClassLoader(system, name);

        Class<?> app = Greeter.class;
        Class<?> a1 = loaderA.loadClass(name);
        Class<?> a2 = loaderA.loadClass(name);
        Class<?> b = loaderB.loadClass(name);

        System.out.println("all three have the same name:           "
                + (app.getName().equals(a1.getName()) && a1.getName().equals(b.getName())));
        System.out.println("loader A asked twice -> same Class:     " + (a1 == a2));
        System.out.println("loader A vs loader B -> same Class:     " + (a1 == b));
        System.out.println("loader A vs application -> same Class: " + (a1 == app));
        System.out.println("a1 is defined by loader A:              " + (a1.getClassLoader() == loaderA));
        System.out.println("comparing getName() wrongly says same:  " + a1.getName().equals(b.getName()));
    }
}
