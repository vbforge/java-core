package com.vbforge.practice.practice_4_find_class_and_define_class;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;

/**
 * A loader that overrides findClass (not loadClass): it reads the bytes itself and
 * calls defineClass. Its parent is the application loader's parent, so the parent
 * cannot see the application classes and findClass really gets called.
 * Reused by practice_19 and practice_21.
 */
public class IsolatingClassLoader extends ClassLoader {

    private int definedCount;

    public IsolatingClassLoader() {
        super(ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = ClassBytes.read(name);
        definedCount++;
        return defineClass(name, bytes, 0, bytes.length);
    }

    public int definedCount() {
        return definedCount;
    }
}
