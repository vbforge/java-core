package com.vbforge.practice.practice_11_externalizable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** Externalizable needs a PUBLIC no-arg constructor: missing or package-private -> writing works, reading fails. */
public class NoArgConstructorRequiredDemo {

    public static class NoNoArg implements Externalizable {
        private static final long serialVersionUID = 1L;
        int v;

        public NoNoArg(int v) {
            this.v = v;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(v);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            v = in.readInt();
        }
    }

    public static class PackagePrivateNoArg implements Externalizable {
        private static final long serialVersionUID = 1L;
        int v;

        PackagePrivateNoArg() { // not public
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(v);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            v = in.readInt();
        }
    }

    private static void tryRead(String title, Object obj) throws Exception {
        byte[] data = SerializationHelper.serialize(obj);
        try {
            SerializationHelper.deserialize(data);
            System.out.println(title + ": read OK");
        } catch (InvalidClassException e) {
            System.out.println(title + ": InvalidClassException (" + e.getMessage().replaceAll("^.*: ", "") + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        tryRead("no no-arg constructor     ", new NoNoArg(1));
        tryRead("package-private no-arg    ", new PackagePrivateNoArg());
    }
}
