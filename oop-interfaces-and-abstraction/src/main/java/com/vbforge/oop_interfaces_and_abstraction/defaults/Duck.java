package com.vbforge.oop_interfaces_and_abstraction.defaults;

/**
 * Inherits {@code move()} from two unrelated interfaces, so the compiler demands a decision:
 * the class MUST override {@code move()}, and may pick either version with {@code X.super.move()}.
 */
public class Duck implements Flyer, Swimmer {

    @Override
    public String move() {
        return Flyer.super.move() + " & " + Swimmer.super.move();
    }
}
