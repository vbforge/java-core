package com.vbforge.practice.practice_10_anonymous_to_lambda;

/** The same functional interface supplied as an anonymous class, an expression lambda, a block lambda with return, and a multi-statement lambda. */
public class LambdaFormsDemo {

    public static void main(String[] args) {
        Jumping jumpA = new Jumping() {
            @Override
            public String jump() {
                return "AAA";
            }
        };

        Jumping jumpB = () -> "BBB";

        Jumping jumpC = () -> { return "CCC"; };

        Jumping jumpD = () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Taking a running start...").append("\n").append("Jumps high!").append("\n").append("Attempt success!");
            return sb.toString();
        };

        System.out.println("jumpA = " + jumpA.jump());
        System.out.println("jumpB = " + jumpB.jump());
        System.out.println("jumpC = " + jumpC.jump());
        System.out.println(jumpD.jump());
    }
}

interface Jumping {
    String jump();
}
