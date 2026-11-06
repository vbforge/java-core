package com.vbforge.practice.practice_4_implementing_interfaces;

/**
 * A class that BOTH extends a concrete superclass AND implements an interface
 * that declares the same method signature.
 *
 *   - Base is a concrete class with call(); it knows nothing about Call.
 *   - Call is an interface with call() and a constant NUM.
 *   - Client extends Base implements Call. Its own call() satisfies the interface
 *     and reuses Base's implementation through super.call().
 *   - Called through a Call reference, the runtime dispatches to Client.call().
 */
public class ExtendsClassImplementsInterfaceDemo {

    static class Base {
        int i = -5;

        public void call() {
            System.out.println("call() of BASE: i = " + i);
        }
    }

    interface Call {
        int NUM = 10;      // implicitly public static final
        void call();
    }

    static class Client extends Base implements Call {
        @Override
        public void call() {
            System.out.println("call() of Client: NUM = " + NUM);
            super.call();      // reuse Base's implementation
        }
    }

    public static void main(String[] args) {
        Call object = new Client();
        object.call();
        // Output:
        //   call() of Client: NUM = 10
        //   call() of BASE: i = -5
    }
}
