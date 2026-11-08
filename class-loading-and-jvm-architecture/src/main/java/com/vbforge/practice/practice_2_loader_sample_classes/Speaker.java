package com.vbforge.practice.practice_2_loader_sample_classes;

/**
 * Shared fixture (not a concept): the interface that "host" and "plugin" agree on.
 * Keeping the contract in an interface that only the parent loader owns is the
 * standard way to talk to a class that a different loader defined.
 */
public interface Speaker {

    String greet();
}
