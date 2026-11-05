package com.vbforge.practice.practice_3_object_tostring;

/**
 * Default {@code Object.toString()}: {@code getClass().getName() + "@" + Integer.toHexString(hashCode())}.
 * The hex part changes between runs, so only the stable prefix is printed here.
 */
public class DefaultToStringDemo {

    // --- Default toString: no override ---
    static final class NoOverride {
        int x = 42;
    }

    // --- Manual reproduction of the default form, for contrast ---
    static final class ReproducesDefault {
        @Override
        public String toString() {
            return getClass().getName() + "@" + Integer.toHexString(hashCode());
        }
    }

    public static void main(String[] args) {
        Object bare = new Object();
        System.out.println("new Object() prefix:  " + prefix(bare.toString()));

        NoOverride n = new NoOverride();
        String text = n.toString();
        System.out.println("n.toString() prefix:  " + prefix(text));
        System.out.println("implicit concat uses the same text: " + (("value=" + n).equals("value=" + text)));

        String hashBasedForm = n.getClass().getName() + "@" + Integer.toHexString(n.hashCode());
        System.out.println("equals class@hex(hashCode): " + text.equals(hashBasedForm));

        System.out.println("manual reproduction prefix: " + prefix(new ReproducesDefault().toString()));
    }

    private static String prefix(String s) {
        return s.substring(0, s.indexOf('@')) + "@<hex>";
    }
}
