package com.vbforge.reflection_api;

import com.vbforge.reflection_api.DynamicProxyDemo.Calculator;
import com.vbforge.reflection_api.DynamicProxyDemo.MapRepository;
import com.vbforge.reflection_api.DynamicProxyDemo.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DynamicProxyTasks — your implementations")
class DynamicProxyTasksTest {

    /** Calculator that remembers how often each method really ran. */
    static class CountingCalculator implements Calculator {
        int addCalls;
        int divideCalls;
        int nameCalls;

        @Override
        public int add(int a, int b) {
            addCalls++;
            return a + b;
        }

        @Override
        public int divide(int a, int b) {
            divideCalls++;
            return a / b;
        }

        @Override
        public String name() {
            nameCalls++;
            return "counting";
        }
    }

    /** Repository that counts real {@code get} executions. */
    static class CountingRepository implements Repository {
        int getCalls;

        @Override
        public String get(String key) {
            getCalls++;
            return "value-of-" + key;
        }

        @Override
        public void put(String key, String value) { }

        @Override
        public void remove(String key) { }
    }

    private DynamicProxyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DynamicProxyTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countingProxy()")
    @DisplayName("countingProxy")
    class CountingProxyTest {

        @Test
        @DisplayName("add twice, name once → {add=2, name=1}")
        void counts() {
            Map<String, Integer> counters = new HashMap<String, Integer>();
            Calculator calc = tasks.countingProxy(new CountingCalculator(), Calculator.class, counters);

            calc.add(1, 2);
            calc.add(3, 4);
            calc.name();

            assertThat(counters).containsOnlyKeys("add", "name");
            assertThat(counters).containsEntry("add", 2);
            assertThat(counters).containsEntry("name", 1);
        }

        @Test
        @DisplayName("calls are delegated and results returned")
        void delegates() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.countingProxy(real, Calculator.class, new HashMap<String, Integer>());

            assertThat(calc.add(2, 3)).isEqualTo(5);
            assertThat(calc.name()).isEqualTo("counting");
            assertThat(real.addCalls).isEqualTo(1);
            assertThat(real.nameCalls).isEqualTo(1);
        }

        @Test
        @DisplayName("a failing call is counted and the original exception is rethrown")
        void failingCall() {
            Map<String, Integer> counters = new HashMap<String, Integer>();
            Calculator calc = tasks.countingProxy(new CountingCalculator(), Calculator.class, counters);

            assertThatThrownBy(() -> calc.divide(1, 0)).isInstanceOf(ArithmeticException.class);

            assertThat(counters).containsEntry("divide", 1);
        }

        @Test
        @DisplayName("the result really is a JDK proxy, not the target itself")
        void isProxy() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.countingProxy(real, Calculator.class, new HashMap<String, Integer>());

            assertThat(Proxy.isProxyClass(calc.getClass())).isTrue();
            assertThat(calc).isNotSameAs(real);
        }

        @Test
        @DisplayName("works for any interface, e.g. Repository")
        void otherInterface() {
            Map<String, Integer> counters = new HashMap<String, Integer>();
            Repository repo = tasks.countingProxy(new MapRepository(), Repository.class, counters);

            repo.put("a", "1");
            repo.put("b", "2");
            repo.remove("a");

            assertThat(counters).containsEntry("put", 2);
            assertThat(counters).containsEntry("remove", 1);
            assertThat(repo.get("b")).isEqualTo("2");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement memoizingProxy()")
    @DisplayName("memoizingProxy")
    class MemoizingProxyTest {

        @Test
        @DisplayName("add(2, 3) twice → executed once, both return 5")
        void sameArgumentsCached() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.memoizingProxy(real, Calculator.class);

            assertThat(calc.add(2, 3)).isEqualTo(5);
            assertThat(calc.add(2, 3)).isEqualTo(5);

            assertThat(real.addCalls).isEqualTo(1);
        }

        @Test
        @DisplayName("different arguments are not mixed up: add(3, 2) is executed")
        void differentArguments() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.memoizingProxy(real, Calculator.class);

            calc.add(2, 3);
            calc.add(3, 2);

            assertThat(real.addCalls).isEqualTo(2);
        }

        @Test
        @DisplayName("no-arg methods are cached too (args is null)")
        void noArgMethod() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.memoizingProxy(real, Calculator.class);

            assertThat(calc.name()).isEqualTo("counting");
            assertThat(calc.name()).isEqualTo("counting");

            assertThat(real.nameCalls).isEqualTo(1);
        }

        @Test
        @DisplayName("the cache is per method: add(8, 2) and divide(8, 2) are different calls")
        void perMethod() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.memoizingProxy(real, Calculator.class);

            assertThat(calc.add(8, 2)).isEqualTo(10);
            assertThat(calc.divide(8, 2)).isEqualTo(4);
            assertThat(calc.divide(8, 2)).isEqualTo(4);

            assertThat(real.addCalls).isEqualTo(1);
            assertThat(real.divideCalls).isEqualTo(1);
        }

        @Test
        @DisplayName("arguments are compared with equals, not ==")
        void equalButNotSameArguments() {
            CountingRepository real = new CountingRepository();
            Repository repo = tasks.memoizingProxy(real, Repository.class);

            assertThat(repo.get(new String("key"))).isEqualTo("value-of-key");
            assertThat(repo.get(new String("key"))).isEqualTo("value-of-key");

            assertThat(real.getCalls).isEqualTo(1);
        }

        @Test
        @DisplayName("exceptions are not cached: divide(1, 0) runs again and rethrows ArithmeticException")
        void exceptionsNotCached() {
            CountingCalculator real = new CountingCalculator();
            Calculator calc = tasks.memoizingProxy(real, Calculator.class);

            assertThatThrownBy(() -> calc.divide(1, 0)).isInstanceOf(ArithmeticException.class);
            assertThatThrownBy(() -> calc.divide(1, 0)).isInstanceOf(ArithmeticException.class);

            assertThat(real.divideCalls).isEqualTo(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement readOnlyView()")
    @DisplayName("readOnlyView")
    class ReadOnlyViewTest {

        private MapRepository realWithA() {
            MapRepository real = new MapRepository();
            real.put("a", "1");
            return real;
        }

        @Test
        @DisplayName("get is delegated")
        void getDelegates() {
            Repository view = tasks.readOnlyView(realWithA(), Repository.class);

            assertThat(view.get("a")).isEqualTo("1");
        }

        @Test
        @DisplayName("put → UnsupportedOperationException and the target is unchanged")
        void putRejected() {
            MapRepository real = realWithA();
            Repository view = tasks.readOnlyView(real, Repository.class);

            assertThatThrownBy(() -> view.put("a", "2")).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> view.put("b", "3")).isInstanceOf(UnsupportedOperationException.class);

            assertThat(real.get("a")).isEqualTo("1");
            assertThat(real.get("b")).isNull();
        }

        @Test
        @DisplayName("remove → UnsupportedOperationException and the target is unchanged")
        void removeRejected() {
            MapRepository real = realWithA();
            Repository view = tasks.readOnlyView(real, Repository.class);

            assertThatThrownBy(() -> view.remove("a")).isInstanceOf(UnsupportedOperationException.class);

            assertThat(real.get("a")).isEqualTo("1");
        }

        @Test
        @DisplayName("the rule is by name prefix: Calculator.add is rejected, name and divide work")
        void prefixRule() {
            CountingCalculator real = new CountingCalculator();
            Calculator view = tasks.readOnlyView(real, Calculator.class);

            assertThatThrownBy(() -> view.add(1, 2)).isInstanceOf(UnsupportedOperationException.class);
            assertThat(view.name()).isEqualTo("counting");
            assertThat(view.divide(8, 2)).isEqualTo(4);
            assertThat(real.addCalls).isEqualTo(0);
        }

        @Test
        @DisplayName("exceptions of delegated methods are rethrown unwrapped")
        void delegatedExceptionUnwrapped() {
            Calculator view = tasks.readOnlyView(new CountingCalculator(), Calculator.class);

            assertThatThrownBy(() -> view.divide(1, 0)).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("the result really is a JDK proxy")
        void isProxy() {
            Repository view = tasks.readOnlyView(realWithA(), Repository.class);

            assertThat(Proxy.isProxyClass(view.getClass())).isTrue();
        }
    }
}
