package com.vbforge.practice.practice_10_initialization_triggers;

/**
 * Passive uses do NOT initialise the class: a class literal, creating an array of the type,
 * and reading a static field through a subclass name (only the class that DECLARES the
 * field is initialised, the subclass is not).
 */
public class PassiveUseDemo {

    static class OnLiteral {
        static {
            System.out.println("   [OnLiteral.<clinit>]");
        }
    }

    static class OnArray {
        static {
            System.out.println("   [OnArray.<clinit>]");
        }
    }

    static class Parent {
        static int shared = 7;

        static {
            System.out.println("   [Parent.<clinit>]");
        }
    }

    static class Child extends Parent {
        static {
            System.out.println("   [Child.<clinit>]");
        }
    }

    public static void main(String[] args) {
        System.out.println("OnLiteral.class:");
        Class<?> literal = OnLiteral.class;
        System.out.println("   got " + literal.getSimpleName() + ", nothing initialised");

        System.out.println("new OnArray[3]:");
        OnArray[] array = new OnArray[3];
        System.out.println("   array of length " + array.length + ", nothing initialised");

        System.out.println("read Child.shared (declared in Parent):");
        System.out.println("   shared = " + Child.shared);

        System.out.println("new Child() now (Parent is already initialised):");
        new Child();
    }
}
