package com.vbforge.practice.practice_3_interface_inheritance;

/**
 * Interface inheritance:
 *   - ContractChild extends ContractMain, so it inherits the parent's method contracts.
 *   - Any implementation of ContractChild must satisfy both.
 *   - Code that only knows ContractMain cannot call the child-only method.
 */
public class InterfaceInheritanceDemo {

    interface ContractMain {
        void methodMain1();
        void methodMain2();
    }

    interface ContractChild extends ContractMain {
        void methodChild3();
    }

    static final class Implementation implements ContractChild {
        @Override public void methodMain1() { System.out.println("methodMain1"); }
        @Override public void methodMain2() { System.out.println("methodMain2"); }
        @Override public void methodChild3() { System.out.println("methodChild3"); }
    }

    public static void main(String[] args) {
        ContractMain mainRef = new Implementation();
        // mainRef.methodChild3(); // compile error: ContractMain doesn't declare it
        printMainMethods(mainRef);

        ContractChild childRef = new Implementation();
        printChildMethods(childRef);
    }

    private static void printMainMethods(ContractMain contract) {
        contract.methodMain1();
        contract.methodMain2();
    }

    private static void printChildMethods(ContractChild contract) {
        contract.methodChild3();
        contract.methodMain1();
        contract.methodMain2();
    }
}
