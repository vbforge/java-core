package com.vbforge.practice.practice_9_write_read_object_hooks;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/** The hook methods must be PRIVATE with the exact signature; a public writeObject is just an ordinary method. */
public class PublicHookIgnoredDemo {

    static boolean publicHookCalled = false;
    static boolean privateHookCalled = false;

    static class WithPublicHook implements Serializable {
        private static final long serialVersionUID = 1L;

        public void writeObject(ObjectOutputStream out) throws IOException {
            publicHookCalled = true;
            out.defaultWriteObject();
        }
    }

    static class WithPrivateHook implements Serializable {
        private static final long serialVersionUID = 1L;

        private void writeObject(ObjectOutputStream out) throws IOException {
            privateHookCalled = true;
            out.defaultWriteObject();
        }
    }

    public static void main(String[] args) throws Exception {
        SerializationHelper.serialize(new WithPublicHook());
        SerializationHelper.serialize(new WithPrivateHook());

        System.out.println("public writeObject called:  " + publicHookCalled);
        System.out.println("private writeObject called: " + privateHookCalled);
    }
}
