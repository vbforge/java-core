package com.vbforge.practice.practice_5_child_first_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A child-first loader: for the class names it "owns" it defines its own copy from the
 * bytes before asking the parent. Everything else is delegated normally, so shared
 * types (interfaces, java.*) still come from the parent. Reused by practice_6, 7, 8, 20.
 */
public class ChildFirstClassLoader extends ClassLoader {

    private final Set<String> ownedNames;

    public ChildFirstClassLoader(ClassLoader parent, String... ownedClassNames) {
        super(parent);
        this.ownedNames = new HashSet<>(Arrays.asList(ownedClassNames));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loaded = findLoadedClass(name);
            if (loaded == null && ownedNames.contains(name)) {
                byte[] bytes = ClassBytes.read(name);
                loaded = defineClass(name, bytes, 0, bytes.length);
            }
            if (loaded != null) {
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }
        return super.loadClass(name, resolve);
    }
}
