package com.vbforge.oop_interfaces_and_abstraction.basics;

/** Delivers at most {@link Notifier#MAX_LENGTH} characters — the constant is inherited from the interface. */
public class SmsNotifier implements Notifier {

    @Override
    public String send(String message) {
        String body = message.length() > MAX_LENGTH ? message.substring(0, MAX_LENGTH) : message;
        return "SMS: " + body;
    }
}
