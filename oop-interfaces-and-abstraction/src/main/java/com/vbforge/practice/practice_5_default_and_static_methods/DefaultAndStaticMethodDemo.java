package com.vbforge.practice.practice_5_default_and_static_methods;

/**
 * Default and static methods in one interface.
 *   - default print(str) is inherited by implementers and calls the static helper.
 *   - static isNullOrBlank(str) belongs to the interface itself and is NOT inherited.
 */
public class DefaultAndStaticMethodDemo {

    interface LinePrinter {
        default void print(String str) {
            if (!isNullOrBlank(str)) {
                System.out.println("LinePrinter prints: " + str);
            }
        }

        static boolean isNullOrBlank(String str) {
            return str == null || str.trim().isEmpty();
        }
    }

    static class ConsoleLinePrinter implements LinePrinter {
        // inherits default print(str), no override needed
    }

    public static void main(String[] args) {
        LinePrinter obj = new ConsoleLinePrinter();
        obj.print("test");   // printed
        obj.print("");       // skipped: empty
        obj.print("   ");    // skipped: blank
        obj.print(null);     // skipped: null

        // Static methods are called on the interface name, never on an instance or implementer.
        System.out.println("LinePrinter.isNullOrBlank(\"\"):    " + LinePrinter.isNullOrBlank(""));
        System.out.println("LinePrinter.isNullOrBlank(null):  " + LinePrinter.isNullOrBlank(null));
        System.out.println("LinePrinter.isNullOrBlank(\"abc\"): " + LinePrinter.isNullOrBlank("abc"));
        // obj.isNullOrBlank("x");                    // compile error: cannot find symbol
        // ConsoleLinePrinter.isNullOrBlank("x");     // compile error: static methods are not inherited
    }
}
