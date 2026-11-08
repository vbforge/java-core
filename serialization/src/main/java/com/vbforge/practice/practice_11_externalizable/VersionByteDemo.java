package com.vbforge.practice.practice_11_externalizable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * With Externalizable the format is yours, so versioning is yours too: write a version byte first
 * and refuse versions you do not understand.
 */
public class VersionByteDemo {

    public static class Profile implements Externalizable {
        private static final long serialVersionUID = 1L;
        static final int SUPPORTED_VERSION = 1;
        static int writeVersion = 1; // lets the demo pretend a newer writer
        String name;

        public Profile() {
        }

        Profile(String name) {
            this.name = name;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeByte(writeVersion);
            out.writeUTF(name);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            int version = in.readByte();
            if (version > SUPPORTED_VERSION) {
                throw new IOException("unsupported profile version " + version);
            }
            name = in.readUTF();
        }
    }

    public static void main(String[] args) throws Exception {
        Profile ok = SerializationHelper.roundTrip(new Profile("ann"));
        System.out.println("version 1 read back: " + ok.name);

        Profile.writeVersion = 2;
        byte[] fromFuture = SerializationHelper.serialize(new Profile("bob"));
        try {
            SerializationHelper.deserialize(fromFuture);
        } catch (IOException e) {
            System.out.println("version 2 rejected: " + e.getMessage());
        }
    }
}
