package com.vbforge.reflection_api;

import com.vbforge.reflection_api.DynamicAccessDemo.Account;
import com.vbforge.reflection_api.DynamicAccessDemo.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("DynamicAccessDemo — API contracts")
class DynamicAccessDemoTest {

    @Nested
    @DisplayName("1. Reading and writing fields")
    class Fields {

        @Test
        @DisplayName("getField does not see private fields")
        void getFieldIgnoresPrivate() {
            assertThatThrownBy(() -> Account.class.getField("owner"))
                    .isInstanceOf(NoSuchFieldException.class);
        }

        @Test
        @DisplayName("a private field cannot be read from another class without setAccessible")
        void privateNeedsSetAccessible() throws Exception {
            Field owner = Account.class.getDeclaredField("owner");

            assertThatThrownBy(() -> owner.get(new Account("Ann", 50)))
                    .isInstanceOf(IllegalAccessException.class);
        }

        @Test
        @DisplayName("setAccessible(true) unlocks a private field for get and set")
        void readAndWritePrivate() throws Exception {
            Account account = new Account("Ann", 50);
            Field owner = Account.class.getDeclaredField("owner");
            owner.setAccessible(true);

            assertThat(owner.get(account)).isEqualTo("Ann");
            owner.set(account, "Bob");

            assertThat(account.getOwner()).isEqualTo("Bob");
        }

        @Test
        @DisplayName("getInt / setInt access primitive fields without boxing")
        void primitiveAccessors() throws Exception {
            Account account = new Account("Ann", 50);
            Field balance = Account.class.getDeclaredField("balance");
            balance.setAccessible(true);

            balance.setInt(account, 999);

            assertThat(balance.getInt(account)).isEqualTo(999);
            assertThat(account.getBalance()).isEqualTo(999);
        }

        @Test
        @DisplayName("setting a value of the wrong type throws IllegalArgumentException")
        void wrongType() throws Exception {
            Account account = new Account("Ann", 50);
            Field balance = Account.class.getDeclaredField("balance");
            balance.setAccessible(true);

            assertThatThrownBy(() -> balance.set(account, "not a number"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a static field ignores the target — pass null")
        void staticField() throws Exception {
            Field mode = Settings.class.getField("mode");
            try {
                mode.set(null, "prod");

                assertThat(Settings.mode).isEqualTo("prod");
                assertThat(mode.get(null)).isEqualTo("prod");
            } finally {
                mode.set(null, "dev");
            }
            assertThat(Settings.mode).isEqualTo("dev");
        }

        @Test
        @DisplayName("private fields of JDK classes are not readable without setAccessible")
        void jdkPrivateField() throws Exception {
            Field value = String.class.getDeclaredField("value");

            assertThatThrownBy(() -> value.get("abc")).isInstanceOf(IllegalAccessException.class);
        }
    }

    @Nested
    @DisplayName("2. Final fields")
    class FinalFields {

        @Test
        @DisplayName("an instance final field can be overwritten after setAccessible")
        void instanceFinalWritable() throws Exception {
            Account account = new Account("Ann", 50);
            Field currency = Account.class.getDeclaredField("currency");
            currency.setAccessible(true);

            currency.set(account, "EUR");

            assertThat(currency.get(account)).isEqualTo("EUR");
        }

        @Test
        @DisplayName("a constant-variable final field is inlined: the getter still returns the old value")
        void constantInlined() throws Exception {
            Account account = new Account("Ann", 50);
            Field currency = Account.class.getDeclaredField("currency");
            currency.setAccessible(true);

            currency.set(account, "EUR");

            assertThat(account.getCurrency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("a static final field cannot be written, even after setAccessible")
        void staticFinalNotWritable() throws Exception {
            Field max = Account.class.getDeclaredField("MAX_BALANCE");
            max.setAccessible(true);

            assertThatThrownBy(() -> max.set(null, 5)).isInstanceOf(IllegalAccessException.class);
            assertThat(Account.maxBalance()).isEqualTo(1_000_000);
        }
    }

    @Nested
    @DisplayName("3. Invoking methods")
    class Methods {

        @Test
        @DisplayName("invoke calls an instance method with boxed arguments")
        void invokeInstance() throws Exception {
            Account account = new Account("Ann", 50);
            Method deposit = Account.class.getMethod("deposit", int.class);

            deposit.invoke(account, 25);

            assertThat(account.getBalance()).isEqualTo(75);
        }

        @Test
        @DisplayName("overloads are selected by parameter types")
        void overloads() throws Exception {
            Account account = new Account("Ann", 50);
            Method formatInt = Account.class.getMethod("format", int.class);
            Method formatStr = Account.class.getMethod("format", String.class);

            assertThat(formatInt.invoke(account, 7)).isEqualTo("int:7");
            assertThat(formatStr.invoke(account, "x")).isEqualTo("str:x");
        }

        @Test
        @DisplayName("a static method is invoked with a null target; the result is boxed")
        void invokeStatic() throws Exception {
            Method doubleIt = Account.class.getMethod("doubleIt", int.class);

            Object result = doubleIt.invoke(null, 21);

            assertThat(result).isEqualTo(42);
            assertThat(result).isInstanceOf(Integer.class);
        }

        @Test
        @DisplayName("a private method needs getDeclaredMethod + setAccessible")
        void invokePrivate() throws Exception {
            Account account = new Account("Ann", 50);

            assertThatThrownBy(() -> Account.class.getMethod("secret"))
                    .isInstanceOf(NoSuchMethodException.class);

            Method secret = Account.class.getDeclaredMethod("secret");
            secret.setAccessible(true);

            assertThat(secret.invoke(account)).isEqualTo("secret-Ann");
        }

        @Test
        @DisplayName("lookup does not box: getMethod with Integer.class does not find deposit(int)")
        void lookupDoesNotBox() {
            assertThatThrownBy(() -> Account.class.getMethod("deposit", Integer.class))
                    .isInstanceOf(NoSuchMethodException.class);
        }

        @Test
        @DisplayName("repeated lookups return equal but distinct Method objects")
        void methodCopies() throws Exception {
            Method first = Account.class.getMethod("deposit", int.class);
            Method second = Account.class.getMethod("deposit", int.class);

            assertThat(first).isNotSameAs(second);
            assertThat(first).isEqualTo(second);
        }
    }

    @Nested
    @DisplayName("4. Exceptions")
    class Exceptions {

        @Test
        @DisplayName("an exception inside the method arrives wrapped in InvocationTargetException")
        void wrapped() throws Exception {
            Account account = new Account("Ann", 50);
            Method deposit = Account.class.getMethod("deposit", int.class);

            assertThatThrownBy(() -> deposit.invoke(account, -5))
                    .isInstanceOf(InvocationTargetException.class)
                    .hasCauseInstanceOf(IllegalArgumentException.class);
            assertThat(account.getBalance()).isEqualTo(50);
        }

        @Test
        @DisplayName("the real exception is available through getCause()")
        void realExceptionViaCause() throws Exception {
            Account account = new Account("Ann", 50);
            Method deposit = Account.class.getMethod("deposit", int.class);

            Throwable thrown = catchThrowable(() -> deposit.invoke(account, 0));

            assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(thrown.getCause().getMessage()).isEqualTo("amount must be positive: 0");
        }

        @Test
        @DisplayName("an argument of the wrong type throws IllegalArgumentException from invoke itself")
        void wrongArgumentType() throws Exception {
            Account account = new Account("Ann", 50);
            Method deposit = Account.class.getMethod("deposit", int.class);

            assertThatThrownBy(() -> deposit.invoke(account, "text"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a wrong number of arguments throws IllegalArgumentException")
        void wrongArgumentCount() throws Exception {
            Account account = new Account("Ann", 50);
            Method deposit = Account.class.getMethod("deposit", int.class);

            assertThatThrownBy(() -> deposit.invoke(account))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null target for an instance method throws NullPointerException")
        void nullTarget() throws Exception {
            Method deposit = Account.class.getMethod("deposit", int.class);

            assertThatThrownBy(() -> deposit.invoke(null, 5))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("5. Constructors")
    class Constructors {

        @Test
        @DisplayName("getConstructor(...).newInstance(...) calls a public constructor")
        void publicConstructor() throws Exception {
            Constructor<Account> ctor = Account.class.getConstructor(String.class, int.class);

            Account account = ctor.newInstance("Ann", 50);

            assertThat(account.getOwner()).isEqualTo("Ann");
            assertThat(account.getBalance()).isEqualTo(50);
        }

        @Test
        @DisplayName("getConstructor does not find a private constructor")
        void privateInvisible() {
            assertThatThrownBy(() -> Account.class.getConstructor(String.class))
                    .isInstanceOf(NoSuchMethodException.class);
        }

        @Test
        @DisplayName("getDeclaredConstructor + setAccessible reaches the private constructor")
        void privateConstructor() throws Exception {
            Constructor<Account> ctor = Account.class.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);

            Account account = ctor.newInstance("Bob");

            assertThat(account.getOwner()).isEqualTo("Bob");
            assertThat(account.getBalance()).isEqualTo(10);
        }

        @Test
        @DisplayName("constructor lookup does not box either: Integer.class != int.class")
        void lookupDoesNotBox() {
            assertThatThrownBy(() -> Account.class.getConstructor(String.class, Integer.class))
                    .isInstanceOf(NoSuchMethodException.class);
        }

        @Test
        @DisplayName("the no-arg constructor is found with no type arguments")
        void noArgConstructor() throws Exception {
            Account account = Account.class.getDeclaredConstructor().newInstance();

            assertThat(account.getOwner()).isEqualTo("nobody");
        }
    }

    @Nested
    @DisplayName("6. Reflective arrays")
    class Arrays {

        @Test
        @DisplayName("Array.newInstance(int.class, n) really is an int[]")
        void primitiveArray() {
            Object ints = Array.newInstance(int.class, 3);

            assertThat(ints.getClass()).isEqualTo(int[].class);
            assertThat(Array.getLength(ints)).isEqualTo(3);
        }

        @Test
        @DisplayName("Array.setInt / getInt read and write elements")
        void readWrite() {
            Object ints = Array.newInstance(int.class, 3);

            Array.setInt(ints, 0, 7);
            Array.setInt(ints, 2, 9);

            assertThat((int[]) ints).containsExactly(7, 0, 9);
            assertThat(Array.getInt(ints, 2)).isEqualTo(9);
        }

        @Test
        @DisplayName("Array.newInstance(String.class, n) is a String[]")
        void referenceArray() {
            Object strings = Array.newInstance(String.class, 2);
            Array.set(strings, 0, "a");

            assertThat(strings instanceof String[]).isTrue();
            assertThat((String[]) strings).containsExactly("a", null);
        }

        @Test
        @DisplayName("a primitive array is not an Object[]")
        void primitiveArrayIsNotObjectArray() {
            Object ints = Array.newInstance(int.class, 1);

            assertThat(ints instanceof Object[]).isFalse();
        }
    }

    @Nested
    @DisplayName("7. Strong encapsulation (Java 9+)")
    class Encapsulation {

        @Test
        @DisplayName("looking up a private JDK field is allowed")
        void lookupAllowed() throws Exception {
            assertThat(String.class.getDeclaredField("value").getName()).isEqualTo("value");
        }

        @Test
        @DisplayName("setAccessible on a private JDK field throws InaccessibleObjectException")
        void setAccessibleBlocked() throws Exception {
            Field value = String.class.getDeclaredField("value");

            Throwable thrown = catchThrowable(() -> value.setAccessible(true));

            assertThat(thrown).isInstanceOf(RuntimeException.class);
            assertThat(thrown.getClass().getSimpleName()).isEqualTo("InaccessibleObjectException");
        }

        @Test
        @DisplayName("setAccessible on your own classes is never blocked")
        void ownClassesFine() throws Exception {
            Field owner = Account.class.getDeclaredField("owner");

            owner.setAccessible(true);

            assertThat(owner.get(new Account("Ann", 1))).isEqualTo("Ann");
        }
    }
}
