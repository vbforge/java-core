package com.vbforge.practice.practice_2_loader_sample_classes;

import com.vbforge.practice.practice_2_loader_sample_classes.Speaker;

/**
 * Shared fixture (not a concept): a tiny class whose bytes the custom loaders
 * in practice_3..8 and 19..21 read from the classpath and define again.
 */
public class Greeter implements Speaker {

    @Override
    public String greet() {
        return "hello";
    }
}
