package com.vbforge.practice.practice_16_enum_map_usage;

import java.util.EnumMap;
import java.util.function.BinaryOperator;

public class EnumOperationExample {
    public static void main(String[] args) {
        EnumMap<Operation, BinaryOperator<Integer>> operations = new EnumMap<>(Operation.class);
        operations.put(Operation.ADD, (a, b) -> a + b);
        operations.put(Operation.SUBTRACT, (a, b) -> a - b);
        operations.put(Operation.MULTIPLY, (a, b) -> a * b);

        int result = operations.get(Operation.MULTIPLY).apply(4, 5);
        System.out.println("Result: " + result); // 20
    }

    enum Operation {
        ADD, SUBTRACT, MULTIPLY
    }
}
