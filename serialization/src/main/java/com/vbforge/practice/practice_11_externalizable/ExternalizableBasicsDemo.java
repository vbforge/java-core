package com.vbforge.practice.practice_11_externalizable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Externalizable: YOU write and read the fields. On reading, the PUBLIC no-arg constructor runs first,
 * then readExternal(in) fills the object; read in exactly the order you wrote.
 */
public class ExternalizableBasicsDemo {

    public static class Point3 implements Externalizable {
        private static final long serialVersionUID = 1L;
        static int noArgCalls = 0;
        int x;
        int y;
        int z;

        public Point3() { // required, public
            noArgCalls++;
        }

        Point3(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
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
        Point3 p = new Point3(1, 2, 3);
        System.out.println("no-arg constructor calls after new Point3(1,2,3): " + Point3.noArgCalls);

        Point3 copy = SerializationHelper.roundTrip(p);
        System.out.println("no-arg constructor calls after reading: " + Point3.noArgCalls);
        System.out.println("copy: " + copy.x + "," + copy.y + "," + copy.z);
    }
}
