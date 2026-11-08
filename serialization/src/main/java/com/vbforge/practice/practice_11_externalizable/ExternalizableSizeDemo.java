package com.vbforge.practice.practice_11_externalizable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/** Same data, two ways: the default format describes every field in the stream, Externalizable writes only your values. */
public class ExternalizableSizeDemo {

    static class DefaultPoint implements Serializable {
        private static final long serialVersionUID = 1L;
        int x = 1;
        int y = 2;
        int z = 3;
    }

    public static class ManualPoint implements Externalizable {
        private static final long serialVersionUID = 1L;
        int x = 1;
        int y = 2;
        int z = 3;

        public ManualPoint() {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(x);
            out.writeInt(y);
            out.writeInt(z);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            x = in.readInt();
            y = in.readInt();
            z = in.readInt();
        }
    }

    public static void main(String[] args) throws Exception {
        int defaultSize = SerializationHelper.serialize(new DefaultPoint()).length;
        int manualSize = SerializationHelper.serialize(new ManualPoint()).length;

        System.out.println("Serializable (default format): " + defaultSize + " bytes");
        System.out.println("Externalizable:                " + manualSize + " bytes");
        System.out.println("Externalizable is smaller: " + (manualSize < defaultSize));
    }
}
