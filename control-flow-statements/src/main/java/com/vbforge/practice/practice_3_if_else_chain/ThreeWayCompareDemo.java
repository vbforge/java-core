package com.vbforge.practice.practice_3_if_else_chain;

/**
 * Two styles of if-else chain that produce a value:
 *   - assign to a local, return once
 *   - return directly from each branch (early return)
 *
 * Both give the same result: +1, -1 or 0 (same contract as Integer.compare).
 */
public class ThreeWayCompareDemo {

    public static void main(String[] args) {
        int[][] pairs = {{10, 5}, {5, 10}, {5, 5}};
        for (int[] p : pairs) {
            System.out.println(p[0] + " vs " + p[1]
                    + " -> test=" + test(p[0], p[1])
                    + ", testEarlyReturn=" + testEarlyReturn(p[0], p[1]));
        }
    }

    static int test(int testval, int target) {
        int result;
        if (testval > target) result = +1;
        else if (testval < target) result = -1;
        else result = 0;
        return result;
    }

    static int testEarlyReturn(int testval, int target) {
        if (testval > target) return +1;
        else if (testval < target) return -1;
        else return 0;
    }
}
