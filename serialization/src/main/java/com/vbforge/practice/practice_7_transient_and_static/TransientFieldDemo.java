package com.vbforge.practice.practice_7_transient_and_static;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** transient fields are skipped: after reading they hold null / 0 / false, NOT the value set by a constructor. */
public class TransientFieldDemo {

    static class Account implements Serializable {
        private static final long serialVersionUID = 1L;
        String user = "ann";
        transient String password = "s3cret";
        transient int loginCount = 7;
        transient boolean admin = true;
    }

    public static void main(String[] args) throws Exception {
        Account a = new Account();
        Account copy = SerializationHelper.roundTrip(a);

        System.out.println("user:       " + copy.user);
        System.out.println("password:   " + copy.password);
        System.out.println("loginCount: " + copy.loginCount);
        System.out.println("admin:      " + copy.admin);
    }
}
