package com.vbforge.practice.practice_16_labeled_break_continue;

/**
 * A label before a loop lets "break label;" or "continue label;" target that specific loop,
 * not just the innermost one.
 *
 * A plain break leaves only the inner loop; a labeled break leaves both.
 */
public class LabeledBreakDemo {

    public static void main(String[] args) {
        System.out.println("--- plain nested loop: break leaves the inner loop only ---");
        plainNested();

        System.out.println("\n--- labeled break: leaves BOTH loops ---");
        labeledOuterBreak();
    }

    static void plainNested() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 1) break;      // only the inner loop stops
                System.out.print("[" + i + "," + j + "] ");
            }
            System.out.println();       // outer loop continues
        }
    }

    static void labeledOuterBreak() {
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    System.out.println("breaking outer at i=" + i + ", j=" + j);
                    break outer;
                }
                System.out.print("[" + i + "," + j + "] ");
            }
        }
        System.out.println("(after the loops)");
    }
}
