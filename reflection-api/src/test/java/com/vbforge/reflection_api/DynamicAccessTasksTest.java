package com.vbforge.reflection_api;

import com.vbforge.reflection_api.DynamicAccessDemo.Account;
import com.vbforge.reflection_api.DynamicAccessDemo.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("DynamicAccessTasks — your implementations")
class DynamicAccessTasksTest {

    /** Field hiding: both classes declare {@code tag}. */
    static class Base {
        protected String tag = "base";
    }

    static class Derived extends Base {
        protected String tag = "derived";
    }

    /** Methods that misbehave in different ways. */
    static class Exploder {
        public void ping() { }

        public void explode() {
            throw new IllegalStateException("boom");
        }

        public void fail() throws Exception {
            throw new Exception("checked");
        }
    }

    /** Constructors that misbehave. */
    static class Fragile {
        public Fragile(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("negative: " + value);
            }
        }
    }

    /** Non-static inner class: carries a synthetic this$0 field. */
    static class Outer {
        class Inner {
            int x = 3;

            Outer outer() {
                return Outer.this;
            }
        }
    }

    private DynamicAccessTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DynamicAccessTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement readField()")
    @DisplayName("readField")
    class ReadFieldTest {

        @Test
        @DisplayName("reads a private field of the object's own class")
        void privateOwnField() {
            assertThat(tasks.readField(new Account("Ann", 50), "owner")).isEqualTo("Ann");
            assertThat(tasks.readField(new SavingsAccount(), "rate")).isEqualTo(5);
        }

        @Test
        @DisplayName("reads a private field declared on a super-class")
        void superClassField() {
            assertThat(tasks.readField(new SavingsAccount(), "balance")).isEqualTo(100);
            assertThat(tasks.readField(new SavingsAccount(), "owner")).isEqualTo("saver");
        }

        @Test
        @DisplayName("primitives come back boxed")
        void boxed() {
            assertThat(tasks.readField(new Account("Ann", 50), "balance")).isInstanceOf(Integer.class);
        }

        @Test
        @DisplayName("the closest declaration wins when a sub-class hides a field")
        void closestWins() {
            assertThat(tasks.readField(new Derived(), "tag")).isEqualTo("derived");
        }

        @Test
        @DisplayName("an unknown field name → IllegalArgumentException")
        void unknownField() {
            assertThatThrownBy(() -> tasks.readField(new Account(), "nope"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement writeField()")
    @DisplayName("writeField")
    class WriteFieldTest {

        @Test
        @DisplayName("writes a private primitive field")
        void privatePrimitive() {
            Account account = new Account("Ann", 50);

            tasks.writeField(account, "balance", 500);

            assertThat(account.getBalance()).isEqualTo(500);
        }

        @Test
        @DisplayName("writes a private field of the object's own class")
        void ownField() {
            SavingsAccount savings = new SavingsAccount();

            tasks.writeField(savings, "rate", 9);

            assertThat(savings.getRate()).isEqualTo(9);
        }

        @Test
        @DisplayName("writes a field declared on a super-class")
        void superClassField() {
            SavingsAccount savings = new SavingsAccount();

            tasks.writeField(savings, "balance", 1);
            tasks.writeField(savings, "owner", "new");

            assertThat(savings.getBalance()).isEqualTo(1);
            assertThat(savings.getOwner()).isEqualTo("new");
        }

        @Test
        @DisplayName("an unknown field name → IllegalArgumentException")
        void unknownField() {
            assertThatThrownBy(() -> tasks.writeField(new Account(), "nope", 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement invokeNoArg()")
    @DisplayName("invokeNoArg")
    class InvokeNoArgTest {

        @Test
        @DisplayName("invokes a private method")
        void privateMethod() {
            assertThat(tasks.invokeNoArg(new Account("Ann", 50), "secret")).isEqualTo("secret-Ann");
        }

        @Test
        @DisplayName("invokes a public method declared on a super-class")
        void superClassPublicMethod() {
            assertThat(tasks.invokeNoArg(new SavingsAccount(), "getOwner")).isEqualTo("saver");
        }

        @Test
        @DisplayName("invokes a private method declared on a super-class")
        void superClassPrivateMethod() {
            assertThat(tasks.invokeNoArg(new SavingsAccount(), "secret")).isEqualTo("secret-saver");
        }

        @Test
        @DisplayName("a void method returns null")
        void voidMethod() {
            assertThat(tasks.invokeNoArg(new Exploder(), "ping")).isNull();
        }

        @Test
        @DisplayName("an unknown method → IllegalArgumentException")
        void unknownMethod() {
            assertThatThrownBy(() -> tasks.invokeNoArg(new Account(), "unknownMethod"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a RuntimeException thrown by the method reaches the caller unwrapped")
        void runtimeExceptionUnwrapped() {
            assertThatThrownBy(() -> tasks.invokeNoArg(new Exploder(), "explode"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("boom");
        }

        @Test
        @DisplayName("a checked exception becomes IllegalStateException with the original as cause")
        void checkedExceptionWrapped() {
            Throwable thrown = catchThrowable(() -> tasks.invokeNoArg(new Exploder(), "fail"));

            assertThat(thrown).isInstanceOf(IllegalStateException.class);
            assertThat(thrown.getCause()).isNotNull();
            assertThat(thrown.getCause().getMessage()).isEqualTo("checked");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement newInstance()")
    @DisplayName("newInstance")
    class NewInstanceTest {

        @Test
        @DisplayName("uses a public constructor")
        void publicConstructor() {
            Account account = tasks.newInstance(Account.class,
                    new Class<?>[] {String.class, int.class}, new Object[] {"Ann", 50});

            assertThat(account.getOwner()).isEqualTo("Ann");
            assertThat(account.getBalance()).isEqualTo(50);
        }

        @Test
        @DisplayName("uses a private constructor")
        void privateConstructor() {
            Account account = tasks.newInstance(Account.class,
                    new Class<?>[] {String.class}, new Object[] {"Bob"});

            assertThat(account.getOwner()).isEqualTo("Bob");
            assertThat(account.getBalance()).isEqualTo(10);
        }

        @Test
        @DisplayName("uses the no-arg constructor")
        void noArgConstructor() {
            Account account = tasks.newInstance(Account.class, new Class<?>[0], new Object[0]);

            assertThat(account.getOwner()).isEqualTo("nobody");
        }

        @Test
        @DisplayName("no matching constructor → IllegalArgumentException")
        void noSuchConstructor() {
            assertThatThrownBy(() -> tasks.newInstance(Account.class,
                    new Class<?>[] {double.class}, new Object[] {1.0}))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a RuntimeException thrown by the constructor is rethrown unwrapped")
        void constructorThrows() {
            assertThatThrownBy(() -> tasks.newInstance(Fragile.class,
                    new Class<?>[] {int.class}, new Object[] {-1}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("negative: -1");
        }

        @Test
        @DisplayName("an abstract class cannot be instantiated → IllegalStateException")
        void abstractClass() {
            assertThatThrownBy(() -> tasks.newInstance(ClassIntrospectionDemo.Animal.class,
                    new Class<?>[] {String.class}, new Object[] {"x"}))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement toFieldMap()")
    @DisplayName("toFieldMap")
    class ToFieldMapTest {

        @Test
        @DisplayName("Account → {owner, balance, currency}; the static constant is excluded")
        void account() {
            Map<String, Object> map = tasks.toFieldMap(new Account("Ann", 50));

            assertThat(map).containsOnlyKeys("owner", "balance", "currency");
            assertThat(map).containsEntry("owner", "Ann");
            assertThat(map).containsEntry("balance", 50);
            assertThat(map).containsEntry("currency", "USD");
        }

        @Test
        @DisplayName("SavingsAccount also includes the super-class fields")
        void savingsAccount() {
            Map<String, Object> map = tasks.toFieldMap(new SavingsAccount());

            assertThat(map).containsOnlyKeys("owner", "balance", "currency", "rate");
            assertThat(map).containsEntry("owner", "saver");
            assertThat(map).containsEntry("balance", 100);
            assertThat(map).containsEntry("rate", 5);
        }

        @Test
        @DisplayName("a sub-class field wins over a hidden super-class field of the same name")
        void shadowedField() {
            Map<String, Object> map = tasks.toFieldMap(new Derived());

            assertThat(map).hasSize(1);
            assertThat(map).containsEntry("tag", "derived");
        }

        @Test
        @DisplayName("synthetic fields such as this$0 are skipped")
        void syntheticSkipped() {
            Map<String, Object> map = tasks.toFieldMap(new Outer().new Inner());

            assertThat(map).containsOnlyKeys("x");
            assertThat(map).containsEntry("x", 3);
        }

        @Test
        @DisplayName("an object without fields → empty map")
        void noFields() {
            assertThat(tasks.toFieldMap(new Object())).isEmpty();
        }
    }
}
