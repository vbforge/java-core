package com.vbforge.practice.practice_12_sealed_records_expression_tree;

import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Add;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Mul;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Neg;
import com.vbforge.practice.practice_12_sealed_records_expression_tree.Expr.Num;

/**
 * simplify: rebuilds the tree bottom-up. Children are simplified first, then record patterns in
 * instanceof test the simplified children (x + 0 = x, x * 1 = x, x * 0 = 0, constant folding).
 */
public class ExprSimplifyDemo {

    static Expr simplify(Expr e) {
        return switch (e) {
            case Num n -> n;
            case Add(var l, var r) -> {
                Expr a = simplify(l), b = simplify(r);
                if (a instanceof Num(int v) && v == 0) yield b;
                if (b instanceof Num(int v) && v == 0) yield a;
                if (a instanceof Num(int x) && b instanceof Num(int y)) yield new Num(x + y);
                yield new Add(a, b);
            }
            case Mul(var l, var r) -> {
                Expr a = simplify(l), b = simplify(r);
                if (a instanceof Num(int v) && v == 0) yield new Num(0);
                if (b instanceof Num(int v) && v == 0) yield new Num(0);
                if (a instanceof Num(int v) && v == 1) yield b;
                if (b instanceof Num(int v) && v == 1) yield a;
                if (a instanceof Num(int x) && b instanceof Num(int y)) yield new Num(x * y);
                yield new Mul(a, b);
            }
            case Neg(var operand) -> {
                Expr s = simplify(operand);
                if (s instanceof Num(int v)) yield new Num(-v);
                yield new Neg(s);
            }
        };
    }

    public static void main(String[] args) {
        Expr e1 = new Add(new Mul(new Num(1), new Num(7)), new Num(0));
        System.out.println(e1 + "  =>  " + simplify(e1));

        Expr e2 = new Mul(new Num(0), new Add(new Num(5), new Num(6)));
        System.out.println(e2 + "  =>  " + simplify(e2));

        Expr e3 = new Neg(new Add(new Num(2), new Num(3)));
        System.out.println(e3 + "  =>  " + simplify(e3));
    }
}
