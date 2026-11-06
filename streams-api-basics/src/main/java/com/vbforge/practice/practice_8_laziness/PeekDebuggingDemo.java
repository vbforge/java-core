package com.vbforge.practice.practice_8_laziness;

import java.util.Arrays;
import java.util.List;

/** peek lets you watch elements pass a stage; use it for debugging only, never for real work. */
public class PeekDebuggingDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("peek: ");
        long matches = words.stream()
                .peek(w -> System.out.print("[peek:" + w + "] "))
                .filter(w -> w.equals("java"))
                .count();
        System.out.println();
        System.out.println("matches: " + matches);
    }
}
