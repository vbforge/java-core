package com.vbforge.practice.practice_21_jmx_memory_beans;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_4_find_class_and_define_class.IsolatingClassLoader;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassLoadingMXBean counts classes the JVM has loaded. Fifty isolated loaders each define
 * Greeter and Speaker (and stay reachable), so the live count grows by at least 100.
 * Absolute numbers differ per JVM, so only relations are printed.
 */
public class ClassLoadingMXBeanDemo {

    public static void main(String[] args) throws Exception {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        long loadedBefore = bean.getLoadedClassCount();

        List<Class<?>> kept = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            kept.add(new IsolatingClassLoader().loadClass(Greeter.class.getName()));
        }

        System.out.println("defined " + kept.size() + " Greeter classes in " + kept.size() + " loaders");
        System.out.println("live class count grew by at least 100: "
                + (bean.getLoadedClassCount() - loadedBefore >= 100));
        System.out.println("total loaded >= currently loaded:      "
                + (bean.getTotalLoadedClassCount() >= bean.getLoadedClassCount()));
        System.out.println("unloaded count is not negative:        " + (bean.getUnloadedClassCount() >= 0));
        System.out.println("verbose class loading is off:          " + !bean.isVerbose());
    }
}
