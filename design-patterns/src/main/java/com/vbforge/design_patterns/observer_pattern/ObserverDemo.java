package com.vbforge.design_patterns.observer_pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * ObserverDemo — notify many listeners when one object changes state.
 *
 * <p>Topics:
 * <ol>
 *   <li>Subject keeps a list of observers; attach/detach/notify</li>
 *   <li>Multiple observers react independently</li>
 *   <li>Detaching stops further notifications</li>
 * </ol>
 */
public class ObserverDemo {

    public static void main(String[] args) {
        attachAndNotifyDemo();
        System.out.println("---");
        multipleObserversDemo();
        System.out.println("---");
        detachDemo();
    }

    /**
     * Baseline: attach one observer, notify, it sees the event.
     *
     * <p><b>Why:</b> decouples the publisher from concrete subscribers.
     * The subject only knows the {@link Observer} interface.
     */
    static void attachAndNotifyDemo() {
        System.out.println("=== Attach & notify ===");
        Subject subject = new Subject();
        List<String> log = new ArrayList<>();
        subject.attach(log::add);
        subject.notifyObservers("hello");
        System.out.println("log: " + log);
    }

    /**
     * Multiple observers all see the event, in registration order.
     */
    static void multipleObserversDemo() {
        System.out.println("=== Multiple observers ===");
        Subject subject = new Subject();
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        subject.attach(a::add);
        subject.attach(b::add);
        subject.notifyObservers("event-1");
        subject.notifyObservers("event-2");
        System.out.println("observer A: " + a);
        System.out.println("observer B: " + b);
    }

    /**
     * Detached observer stops receiving events.
     */
    static void detachDemo() {
        System.out.println("=== Detach ===");
        Subject subject = new Subject();
        List<String> kept    = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        Observer o1 = kept::add;
        Observer o2 = removed::add;
        subject.attach(o1);
        subject.attach(o2);
        subject.detach(o2);
        subject.notifyObservers("only-kept");
        System.out.println("kept    : " + kept);
        System.out.println("removed : " + removed);
    }

    // ── Shared types ─────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface Observer {
        void update(String event);
    }

    public static class Subject {
        private final List<Observer> observers = new ArrayList<>();

        public void attach(Observer o) { observers.add(o); }
        public void detach(Observer o) { observers.remove(o); }
        public int size()              { return observers.size(); }

        public void notifyObservers(String event) {
            for (Observer o : observers) o.update(event);
        }
    }
}