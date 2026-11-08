package com.vbforge.practice.practice_12_interface_initialization;

/**
 * Initialising a class does not initialise the interfaces it implements, with one exception:
 * a superinterface that declares a default method is initialised first. An interface is
 * also initialised when one of its non-constant fields is read.
 */
public class InterfaceInitializationDemo {

    static int log(String what) {
        System.out.println("   [init] " + what);
        return 1;
    }

    interface PlainInterface {
        int VALUE = log("PlainInterface");
    }

    interface DefaultInterface {
        int VALUE = log("DefaultInterface");

        default void hello() {
        }
    }

    static class PlainImpl implements PlainInterface {
        static {
            log("PlainImpl");
        }
    }

    static class DefaultImpl implements DefaultInterface {
        static {
            log("DefaultImpl");
        }
    }

    public static void main(String[] args) {
        System.out.println("new PlainImpl()   (superinterface has no default method):");
        new PlainImpl();

        System.out.println("new DefaultImpl() (superinterface declares a default method):");
        new DefaultImpl();

        System.out.println("PlainInterface.VALUE read directly:");
        System.out.println("   VALUE = " + PlainInterface.VALUE);
    }
}
