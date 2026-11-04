package com.vbforge.practice.practice_9_precedence_and_expression_pitfalls;

/**
 * Operator precedence and associativity reference.
 * When in doubt, parenthesise — a reader should never have to reach for this table.
 */
public class OperatorPrecedenceTableDemo {

    public static void main(String[] args) {
        int ans = 10 + 5 * 4 / 2 % 3 + 7 - 4;
        System.out.println("ans = " + ans);

        System.out.println("""
                Step 1: 5 * 4     → 10 + 20 / 2 % 3 + 7 - 4
                Step 2: 20 / 2    → 10 + 10 % 3 + 7 - 4
                Step 3: 10 % 3    → 10 + 1 + 7 - 4
                Step 4: 10 + 1    → 11 + 7 - 4
                Step 5: 11 + 7    → 18 - 4
                Step 6: 18 - 4    → 14
                """);

        System.out.println("""
                Precedence (high → low):
                  1  ()  []  .
                  2  unary - +  ++  --  !  ~    (right-to-left)
                  3  (type cast)
                  4  *  /  %
                  5  +  -
                  6  <<  >>  >>>
                  7  <  <=  >  >=  instanceof
                  8  ==  !=
                  9  &
                 10  ^
                 11  |
                 12  &&
                 13  ||
                 14  ?:                         (right-to-left)
                 15  =  +=  -=  *=  /=  %=  ...  (right-to-left)
                """);
    }
}
