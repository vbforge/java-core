package com.vbforge.practice.practice_7_class_cast_exception_across_loaders;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_2_loader_sample_classes.Speaker;
import com.vbforge.practice.practice_5_child_first_loader.ChildFirstClassLoader;

/**
 * "X cannot be cast to X": the plugin object is a Greeter of ANOTHER loader, so the cast
 * to the host's Greeter fails. Casting to the shared interface works.
 */
public class CastAcrossLoadersDemo {

    public static void main(String[] args) throws Exception {
        String name = Greeter.class.getName();
        ChildFirstClassLoader pluginLoader = new ChildFirstClassLoader(ClassLoader.getSystemClassLoader(), name);
        Object plugin = pluginLoader.loadClass(name).getDeclaredConstructor().newInstance();

        System.out.println("runtime class name equals Greeter's name: "
                + plugin.getClass().getName().equals(name));

        try {
            Greeter greeter = (Greeter) plugin;
            System.out.println("cast worked: " + greeter.greet());
        } catch (ClassCastException e) {
            System.out.println("(Greeter) plugin -> ClassCastException");
            System.out.println("message says 'cannot be cast': " + e.getMessage().contains("cannot be cast"));
        }

        Speaker speaker = (Speaker) plugin;
        System.out.println("(Speaker) plugin works, greet() = " + speaker.greet());
    }
}
