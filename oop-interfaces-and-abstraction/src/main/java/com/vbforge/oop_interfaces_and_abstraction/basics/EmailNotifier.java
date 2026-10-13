package com.vbforge.oop_interfaces_and_abstraction.basics;

/** Delivers the whole message, whatever its length. */
public class EmailNotifier implements Notifier {

    @Override
    public String send(String message) {            // must be public: an interface method cannot be narrowed
        return "EMAIL: " + message;
    }
}
