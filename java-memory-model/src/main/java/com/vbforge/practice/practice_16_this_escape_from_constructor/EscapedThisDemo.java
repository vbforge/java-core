package com.vbforge.practice.practice_16_this_escape_from_constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * `this` escapes when a constructor hands it out before construction is finished. Here the
 * registry runs INSIDE the constructor, before {@code value} is assigned, so it sees 0 even
 * though the field is final. The final-field guarantee only applies once the constructor has
 * finished. (With a real listener on another thread this is also a data race.)
 * Single-threaded on purpose: the output is the same on every run.
 */
public class EscapedThisDemo {

    static class Counter {
        final int value;

        Counter(Consumer<Counter> registry) {
            registry.accept(this);          // `this` escapes: constructor not finished
            this.value = 42;
        }
    }

    public static void main(String[] args) {
        List<Integer> seenByRegistry = new ArrayList<>();

        Counter counter = new Counter(c -> seenByRegistry.add(c.value));

        System.out.println("value seen by the registry:  " + seenByRegistry.get(0));  // 0
        System.out.println("value after construction:    " + counter.value);           // 42
    }
}
