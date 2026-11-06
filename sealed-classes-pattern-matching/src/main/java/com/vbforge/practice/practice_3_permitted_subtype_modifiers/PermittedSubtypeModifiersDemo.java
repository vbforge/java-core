package com.vbforge.practice.practice_3_permitted_subtype_modifiers;

import java.lang.reflect.Modifier;

/**
 * Every permitted subtype must choose one of: final, sealed, non-sealed.
 *   - final       -> closed leaf (a record is implicitly final)
 *   - sealed      -> continues the closed hierarchy (see practice_4)
 *   - non-sealed  -> deliberately re-opens: anyone may extend it
 *
 * A permitted subtype with none of these modifiers does not compile.
 */
public class PermittedSubtypeModifiersDemo {

    sealed interface Payment permits Card, Cash, Custom {}

    static final class Card implements Payment {}                 // final leaf
    record Cash(int amount) implements Payment {}                 // implicitly final
    static non-sealed class Custom implements Payment {}          // re-opened
    static class Crypto extends Custom {}                         // allowed only because Custom is non-sealed

    static String describe(Payment p) {
        return switch (p) {
            case Card c   -> "card";
            case Cash c   -> "cash " + c.amount();
            case Custom c -> "custom (" + c.getClass().getSimpleName() + ")";
        };
    }

    public static void main(String[] args) {
        System.out.println("Payment.isSealed()  = " + Payment.class.isSealed());
        System.out.println("Card is final       = " + Modifier.isFinal(Card.class.getModifiers()));
        System.out.println("Cash is final       = " + Modifier.isFinal(Cash.class.getModifiers()) + " (record)");
        System.out.println("Custom.isSealed()   = " + Custom.class.isSealed() + " (non-sealed)");

        System.out.println();
        System.out.println(describe(new Card()));
        System.out.println(describe(new Cash(50)));
        System.out.println(describe(new Custom()));
        System.out.println(describe(new Crypto()));   // an unknown subclass lands in the Custom branch
    }
}
