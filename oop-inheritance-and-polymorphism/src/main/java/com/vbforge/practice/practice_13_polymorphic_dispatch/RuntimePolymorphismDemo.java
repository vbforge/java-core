package com.vbforge.practice.practice_13_polymorphic_dispatch;

import java.util.List;

/**
 * Runtime polymorphism (dynamic dispatch) - method overriding.
 *
 *   - a subclass provides its own version of a parent method
 *   - resolved at RUNTIME by the actual object type, not by the reference type
 *   - foundation of the Open/Closed Principle: new Notification types need no change in dispatch()
 */
public class RuntimePolymorphismDemo {

    static abstract class Notification {
        protected String recipient;
        Notification(String recipient) { this.recipient = recipient; }
        public abstract void send(String message);
        public String getRecipient() { return recipient; }
    }
    static class EmailNotification extends Notification {
        EmailNotification(String email) { super(email); }
        @Override public void send(String m) { System.out.println("EMAIL  -> " + recipient + " : " + m); }
    }
    static class SmsNotification extends Notification {
        SmsNotification(String phone) { super(phone); }
        @Override public void send(String m) { System.out.println("SMS    -> " + recipient + " : " + m); }
    }
    static class PushNotification extends Notification {
        PushNotification(String device) { super(device); }
        @Override public void send(String m) { System.out.println("PUSH   -> " + recipient + " : " + m); }
    }

    static void dispatch(Notification notification, String message) {
        notification.send(message);
    }

    public static void main(String[] args) {
        List<Notification> notifications = List.of(
            new EmailNotification("alice@example.com"),
            new SmsNotification("+1-555-0100"),
            new PushNotification("device-xyz-42")
        );

        for (Notification n : notifications) {
            dispatch(n, "Your order has shipped!");
        }

        System.out.println("\nSame reference type, different runtime behavior:");
        Notification n;
        n = new EmailNotification("bob@example.com");
        n.send("Hello via email");
        n = new SmsNotification("+1-555-0200");
        n.send("Hello via SMS");
    }
}
