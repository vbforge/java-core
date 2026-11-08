package com.vbforge.practice.practice_5_child_first_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_2_loader_sample_classes.Speaker;
import com.vbforge.practice.practice_3_parent_first_delegation.TracingClassLoader;

/**
 * Same request, two strategies. Parent-first returns the application's Greeter.
 * Child-first defines its own Greeter but still shares Speaker with the parent,
 * which is what makes a call through the interface possible.
 */
public class ChildFirstVersusParentFirstDemo {

    public static void main(String[] args) throws Exception {
        String greeterName = Greeter.class.getName();
        ClassLoader system = ClassLoader.getSystemClassLoader();

        TracingClassLoader parentFirst = new TracingClassLoader(system);
        ChildFirstClassLoader childFirst = new ChildFirstClassLoader(system, greeterName);

        Class<?> viaParentFirst = parentFirst.loadClass(greeterName);
        Class<?> viaChildFirst = childFirst.loadClass(greeterName);

        System.out.println("parent-first: same Class as Greeter.class: " + (viaParentFirst == Greeter.class));
        System.out.println("child-first:  same Class as Greeter.class: " + (viaChildFirst == Greeter.class));
        System.out.println("child-first:  defined by the child loader: " + (viaChildFirst.getClassLoader() == childFirst));

        Object instance = viaChildFirst.getDeclaredConstructor().newInstance();
        System.out.println("instance is a Speaker (not owned, shared with the parent): " + Speaker.class.isInstance(instance));
        System.out.println("instance is the application's Greeter (owned, so it is not): " + Greeter.class.isInstance(instance));
        System.out.println("its interface is the application's Speaker: " + (viaChildFirst.getInterfaces()[0] == Speaker.class));
    }
}
