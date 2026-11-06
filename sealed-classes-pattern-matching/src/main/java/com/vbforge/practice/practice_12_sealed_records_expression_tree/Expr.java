package com.vbforge.practice.practice_12_sealed_records_expression_tree;

/** Sealed interface + records = an algebraic data type (a small expression tree). */
public sealed interface Expr permits Expr.Num, Expr.Add, Expr.Mul, Expr.Neg {
    record Num(int value) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Mul(Expr left, Expr right) implements Expr {}
    record Neg(Expr operand) implements Expr {}
}
