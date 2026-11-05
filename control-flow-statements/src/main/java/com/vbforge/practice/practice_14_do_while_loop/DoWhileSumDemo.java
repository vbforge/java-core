package com.vbforge.practice.practice_14_do_while_loop;

/**
 * Sum 1..N with do-while. Because the body runs before the check, an upper bound below 1
 * still adds the first value: sumOneTo(0) returns 1, not 0. A for or while loop would return 0.
 */
public class DoWhileSumDemo {

    public static void main(String[] args) {
        System.out.println("sumOneTo(5) = " + sumOneTo(5));
        System.out.println("sumOneTo(0) = " + sumOneTo(0) + "  (body ran once before the check)");
    }

    static int sumOneTo(int highBorder) {
        int s = 0;
        int i = 1;
        do {
            s += i;
            i++;
        } while (i <= highBorder);
        return s;
    }
}
