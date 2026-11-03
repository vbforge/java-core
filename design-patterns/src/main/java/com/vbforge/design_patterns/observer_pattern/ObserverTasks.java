package com.vbforge.design_patterns.observer_pattern;

import java.util.List;

/**
 * ObserverTasks — implement every method below to make {@code ObserverTasksTest} go GREEN.
 */
public class ObserverTasks {

    @FunctionalInterface
    public interface Observer {
        void update(String event);
    }

    public interface Subject {
        void attach(Observer o);
        void detach(Observer o);
        void notifyObservers(String event);
        int size();
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a fresh {@link Subject} that stores observers and notifies them
     * on each {@code notifyObservers} call, in registration order.
     *
     * <p>Examples:
     * <pre>
     *   Subject s = createSubject();
     *   List&lt;String&gt; got = new ArrayList&lt;&gt;();
     *   s.attach(got::add);
     *   s.notifyObservers("hi");
     *   got          → ["hi"]
     *   s.size()     → 1
     *
     *   s.detach(got::add)              // no-op if not the same reference
     *   s.notifyObservers("bye");       // got still receives "bye"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use an {@code ArrayList<Observer>} field.</li>
     *   <li>{@code attach} appends; {@code detach} uses {@code remove(o)} —
     *       identity/equals decides what "the same observer" means.</li>
     *   <li>Iterate a snapshot copy or use a plain for-each — an observer
     *       that detaches itself during {@code notifyObservers} would throw
     *       {@code ConcurrentModificationException} otherwise.</li>
     * </ul>
     *
     * @return a new, empty {@link Subject}
     */
    public Subject createSubject() {
        throw new UnsupportedOperationException("TODO: implement createSubject");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns an {@link Observer} whose {@code update} appends the event to
     * the supplied list. The same list must receive every event, in order.
     *
     * <p>Examples:
     * <pre>
     *   List&lt;String&gt; log = new ArrayList&lt;&gt;();
     *   Observer o = createListObserver(log);
     *   o.update("a");
     *   o.update("b");
     *   log          → ["a", "b"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A lambda {@code event -> list.add(event)} is all that's needed.</li>
     *   <li>Do not copy the list — the caller expects to see the events appear.</li>
     * </ul>
     *
     * @param list the list that will receive every event
     * @return an {@link Observer} that appends to {@code list}
     */
    public Observer createListObserver(List<String> list) {
        throw new UnsupportedOperationException("TODO: implement createListObserver");
    }
}