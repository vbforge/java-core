package com.vbforge.practice.practice_10_initialization_triggers;

/**
 * Active uses that initialise a class: new, a static method call, reading or writing a
 * non-constant static field. Each nested class announces its static initializer.
 */
public class ActiveUseTriggersDemo {

    static class OnNew {
        static {
            System.out.println("   [OnNew.<clinit>]");
        }
    }

    static class OnStaticMethod {
        static {
            System.out.println("   [OnStaticMethod.<clinit>]");
        }

        static void call() {
        }
    }

    static class OnStaticFieldRead {
        static int value = 1;

        static {
            System.out.println("   [OnStaticFieldRead.<clinit>]");
        }
    }

    static class OnStaticFieldWrite {
        static int value;

        static {
            System.out.println("   [OnStaticFieldWrite.<clinit>]");
        }
    }

    public static void main(String[] args) {
        System.out.println("new OnNew():");
        new OnNew();

        System.out.println("OnStaticMethod.call():");
        OnStaticMethod.call();

        System.out.println("read OnStaticFieldRead.value:");
        int read = OnStaticFieldRead.value;
        System.out.println("   value = " + read);

        System.out.println("write OnStaticFieldWrite.value = 2:");
        OnStaticFieldWrite.value = 2;
    }
}
