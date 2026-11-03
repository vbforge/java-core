package com.vbforge.design_patterns.observer_pattern;

import com.vbforge.design_patterns.observer_pattern.ObserverTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ObserverTasks — your implementations")
class ObserverTasksTest {

    private ObserverTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ObserverTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement createSubject()")
    @DisplayName("Task 1 — createSubject()")
    class SubjectTest {

        @Test
        @DisplayName("initially has zero observers")
        void initialSize() {
            assertThat(tasks.createSubject().size()).isZero();
        }

        @Test
        @DisplayName("attach increments size")
        void attachIncrements() {
            ObserverTasks.Subject s = tasks.createSubject();
            s.attach(e -> {});
            assertThat(s.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("notify delivers to every observer in order")
        void notifyDelivers() {
            ObserverTasks.Subject s = tasks.createSubject();
            List<String> a = new ArrayList<>();
            List<String> b = new ArrayList<>();
            s.attach(a::add);
            s.attach(b::add);
            s.notifyObservers("e");
            assertThat(a).containsExactly("e");
            assertThat(b).containsExactly("e");
        }

        @Test
        @DisplayName("detach stops delivery")
        void detachStops() {
            ObserverTasks.Subject s = tasks.createSubject();
            List<String> got = new ArrayList<>();
            ObserverTasks.Observer o = got::add;
            s.attach(o);
            s.detach(o);
            s.notifyObservers("x");
            assertThat(got).isEmpty();
        }

        @Test
        @DisplayName("each createSubject() is independent")
        void independent() {
            ObserverTasks.Subject s1 = tasks.createSubject();
            ObserverTasks.Subject s2 = tasks.createSubject();
            s1.attach(e -> {});
            assertThat(s1.size()).isEqualTo(1);
            assertThat(s2.size()).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement createListObserver()")
    @DisplayName("Task 2 — createListObserver()")
    class ListObserverTest {

        @Test
        @DisplayName("appends each event to the list")
        void appends() {
            List<String> log = new ArrayList<>();
            ObserverTasks.Observer o = tasks.createListObserver(log);
            o.update("a");
            o.update("b");
            assertThat(log).containsExactly("a", "b");
        }

        @Test
        @DisplayName("same list instance receives events (no copy)")
        void sameInstance() {
            List<String> log = new ArrayList<>();
            tasks.createListObserver(log).update("x");
            assertThat(log).containsExactly("x");
        }

        @Test
        @DisplayName("works with the Subject from Task 1")
        void withSubject() {
            ObserverTasks.Subject s = tasks.createSubject();
            List<String> log = new ArrayList<>();
            s.attach(tasks.createListObserver(log));
            s.notifyObservers("hi");
            assertThat(log).containsExactly("hi");
        }
    }
}