package com.vbforge.practice.practice_16_this_escape_from_constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The fix for the `this` escape: finish construction first, then register from a static
 * factory. The registry only ever sees a completely built object.
 */
public class StaticFactoryRegistrationDemo {

    static class Counter {
        final int value;

        private Counter() {
            this.value = 42;
        }

        static Counter create(Consumer<Counter> registry) {
            Counter c = new Counter();      // construction is finished here
            registry.accept(c);             // only now does anybody else see it
            return c;
        }
    }

    public static void main(String[] args) {
        List<Integer> seenByRegistry = new ArrayList<>();

        Counter counter = Counter.create(c -> seenByRegistry.add(c.value));

        System.out.println("value seen by the registry:  " + seenByRegistry.get(0));  // 42
        System.out.println("value after construction:    " + counter.value);           // 42
    }
}
