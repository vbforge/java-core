package com.vbforge.practice.practice_16_labeled_break_continue;

/**
 * Labeled break / continue with while loops - same rules as with for.
 *
 *   - plain continue   -> restart the innermost loop
 *   - labeled continue -> jump to the label and restart that loop
 *   - plain break      -> exit the innermost loop
 *   - labeled break    -> exit the loop the label is attached to
 */
public class LabeledWhileDemo {

    public static void main(String[] args) {
        int i = 0;

        outer:
        while (true) {
            System.out.println("outer iteration");
            while (true) {
                i++;
                System.out.println("  i = " + i);

                if (i == 1) {
                    System.out.println("  plain continue");
                    continue;                 // restart the inner while
                }
                if (i == 3) {
                    System.out.println("  continue outer");
                    continue outer;           // restart the outer while
                }
                if (i == 5) {
                    System.out.println("  plain break");
                    break;                    // exit the inner while
                }
                if (i == 7) {
                    System.out.println("  break outer");
                    break outer;              // exit the outer while
                }
            }
        }
        System.out.println("after the loops, i = " + i);
    }
}
