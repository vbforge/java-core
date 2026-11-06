package com.vbforge.practice.practice_12_sealed_records_expression_tree;

import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Add;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Mul;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Neg;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Num;

/** show: another traversal of the same tree - turns it into a fully parenthesised string. */
public class ExprShowDemo {

    static String show(Expr e) {
        return switch (e) {
            case Num(var v)        -> String.valueOf(v);
            case Add(var l, var r) -> "(" + show(l) + " + " + show(r) + ")";
            case Mul(var l, var r) -> "(" + show(l) + " * " + show(r) + ")";
            case Neg(var operand)  -> "-" + show(operand);
        };
    }

    public static void main(String[] args) {
        System.out.println(show(new Add(new Num(2), new Mul(new Num(3), new Num(4)))));
        System.out.println(show(new Neg(new Add(new Num(2), new Num(3)))));
    }
}
