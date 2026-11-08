package com.vbforge.practice.practice_9_write_read_object_hooks;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Private writeObject/readObject hooks: call defaultWriteObject()/defaultReadObject() FIRST,
 * then write/read your own data in the SAME order. Here a transient password is stored "encrypted"
 * (reversed - a stand-in for real encryption) and a derived transient field is rebuilt on reading.
 */
public class CustomHooksDemo {

    static class Credentials implements Serializable {
        private static final long serialVersionUID = 1L;
        String user;
        transient String password;
        transient int userLength; // derived, never stored

        Credentials(String user, String password) {
            this.user = user;
            this.password = password;
            this.userLength = user.length();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();                                  // user
            out.writeObject(new StringBuilder(password).reverse().toString()); // extra data
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();                                    // user
            password = new StringBuilder((String) in.readObject()).reverse().toString();
            userLength = user.length();                                // rebuild derived state
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] data = SerializationHelper.serialize(new Credentials("ann", "s3cret"));
        String asText = new String(data, "ISO-8859-1");
        System.out.println("plain password visible in the stream: " + asText.contains("s3cret"));

        Credentials copy = SerializationHelper.deserialize(data);
        System.out.println("copy: user=" + copy.user + ", password=" + copy.password + ", userLength=" + copy.userLength);
    }
}
