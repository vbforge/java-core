package com.vbforge.design_patterns.observer_pattern;

import com.vbforge.design_patterns.observer_pattern.ObserverDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ObserverDemo — API contracts")
class ObserverDemoTest {

    @Nested
    @DisplayName("1. Attach and notify")
    class AttachAndNotifyTest {

        @Test
        @DisplayName("attached observer receives the event")
        void receivesEvent() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            List<String> log = new ArrayList<>();
            s.attach(log::add);
            s.notifyObservers("x");
            assertThat(log).containsExactly("x");
        }

        @Test
        @DisplayName("size reflects attach count")
        void size() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            assertThat(s.size()).isZero();
            s.attach(e -> {});
            s.attach(e -> {});
            assertThat(s.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("2. Multiple observers")
    class MultipleTest {

        @Test
        @DisplayName("all observers receive every event")
        void everyone() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            List<String> a = new ArrayList<>();
            List<String> b = new ArrayList<>();
            s.attach(a::add);
            s.attach(b::add);
            s.notifyObservers("e1");
            s.notifyObservers("e2");
            assertThat(a).containsExactly("e1", "e2");
            assertThat(b).containsExactly("e1", "e2");
        }

        @Test
        @DisplayName("observers fire in registration order")
        void order() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            List<String> order = new ArrayList<>();
            s.attach(e -> order.add("A"));
            s.attach(e -> order.add("B"));
            s.notifyObservers("x");
            assertThat(order).containsExactly("A", "B");
        }
    }

    @Nested
    @DisplayName("3. Detach")
    class DetachTest {

        @Test
        @DisplayName("detached observer stops receiving events")
        void stopsReceiving() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            List<String> kept = new ArrayList<>();
            List<String> removed = new ArrayList<>();
            ObserverDemo.Observer o1 = kept::add;
            ObserverDemo.Observer o2 = removed::add;
            s.attach(o1);
            s.attach(o2);
            s.detach(o2);
            s.notifyObservers("x");
            assertThat(kept).containsExactly("x");
            assertThat(removed).isEmpty();
        }

        @Test
        @DisplayName("detach decrements size")
        void size() {
            ObserverDemo.Subject s = new ObserverDemo.Subject();
            ObserverDemo.Observer o = e -> {};
            s.attach(o);
            s.detach(o);
            assertThat(s.size()).isZero();
        }
    }
}