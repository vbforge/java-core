package com.vbforge.practice.practice_12_sealed_records_expression_tree;

import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Add;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Mul;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Neg;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Num;

/** eval: a recursive, exhaustive switch with record patterns - no default, no casts. */
public class ExprEvalDemo {

    static int eval(Expr e) {
        return switch (e) {
            case Num(var v)        -> v;
            case Add(var l, var r) -> eval(l) + eval(r);
            case Mul(var l, var r) -> eval(l) * eval(r);
            case Neg(var operand)  -> -eval(operand);
        };
    }

    public static void main(String[] args) {
        Expr e = new Add(new Num(2), new Mul(new Num(3), new Num(4)));   // 2 + (3 * 4)
        System.out.println(e);
        System.out.println("eval = " + eval(e));
        System.out.println("eval(-(2 + 3)) = " + eval(new Neg(new Add(new Num(2), new Num(3)))));
    }
}
