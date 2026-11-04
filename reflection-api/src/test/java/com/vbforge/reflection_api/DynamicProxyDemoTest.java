package com.vbforge.reflection_api;

import com.vbforge.reflection_api.DynamicProxyDemo.AccessGuardHandler;
import com.vbforge.reflection_api.DynamicProxyDemo.Calculator;
import com.vbforge.reflection_api.DynamicProxyDemo.InMemoryUserService;
import com.vbforge.reflection_api.DynamicProxyDemo.NaiveHandler;
import com.vbforge.reflection_api.DynamicProxyDemo.RecordingHandler;
import com.vbforge.reflection_api.DynamicProxyDemo.RequiresRole;
import com.vbforge.reflection_api.DynamicProxyDemo.SimpleCalculator;
import com.vbforge.reflection_api.DynamicProxyDemo.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("DynamicProxyDemo — API contracts")
class DynamicProxyDemoTest {

    @Nested
    @DisplayName("1. Creating a proxy")
    class Creation {

        @Test
        @DisplayName("the proxy implements the interface but is not the implementation class")
        void typeRelations() {
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThat(proxy instanceof Calculator).isTrue();
            assertThat(((Object) proxy) instanceof SimpleCalculator).isFalse();
            assertThatThrownBy(() -> SimpleCalculator.class.cast(proxy))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("Proxy.isProxyClass and getInvocationHandler reveal the proxy")
        void revealed() {
            RecordingHandler handler = new RecordingHandler(new SimpleCalculator());
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class, handler);

            assertThat(Proxy.isProxyClass(proxy.getClass())).isTrue();
            assertThat(Proxy.getInvocationHandler(proxy)).isSameAs(handler);
            assertThat(Proxy.isProxyClass(SimpleCalculator.class)).isFalse();
        }

        @Test
        @DisplayName("the generated class implements exactly the requested interface")
        void generatedClass() {
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThat(proxy.getClass().getInterfaces()).containsExactly(Calculator.class);
            assertThat(proxy.getClass().getSimpleName()).startsWith("$Proxy");
        }

        @Test
        @DisplayName("the proxy class is generated once per interface set and reused")
        void classIsCached() {
            Calculator first = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));
            Calculator second = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThat(first).isNotSameAs(second);
            assertThat(first.getClass()).isSameAs(second.getClass());
        }

        @Test
        @DisplayName("only interfaces can be proxied: a class → IllegalArgumentException")
        void classRejected() {
            assertThatThrownBy(() -> Proxy.newProxyInstance(Calculator.class.getClassLoader(),
                    new Class<?>[] {SimpleCalculator.class},
                    new RecordingHandler(new SimpleCalculator())))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("2. Interception")
    class Interception {

        @Test
        @DisplayName("results are passed through from the real object")
        void resultPassedThrough() {
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThat(calc.add(2, 3)).isEqualTo(5);
            assertThat(calc.name()).isEqualTo("simple");
        }

        @Test
        @DisplayName("the handler sees every call in order")
        void callsRecorded() {
            RecordingHandler handler = new RecordingHandler(new SimpleCalculator());
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class, handler);

            calc.add(2, 3);
            calc.name();
            calc.divide(10, 2);

            assertThat(handler.calls()).containsExactly("add(2, 3)", "name()", "divide(10, 2)");
        }

        @Test
        @DisplayName("args is null — not an empty array — for a method without parameters")
        void argsNullForNoArgMethods() {
            final Object[][] captured = new Object[1][];
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    (proxy, method, args) -> {
                        captured[0] = args;
                        return "intercepted";
                    });

            assertThat(calc.name()).isEqualTo("intercepted");
            assertThat(captured[0]).isNull();
        }

        @Test
        @DisplayName("the handler receives the interface Method and boxed arguments")
        void methodAndBoxedArguments() {
            final Method[] seenMethod = new Method[1];
            final Object[][] seenArgs = new Object[1][];
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    (proxy, method, args) -> {
                        seenMethod[0] = method;
                        seenArgs[0] = args;
                        return 99;
                    });

            assertThat(calc.add(4, 5)).isEqualTo(99);
            assertThat(seenMethod[0].getName()).isEqualTo("add");
            assertThat(seenMethod[0].getDeclaringClass()).isEqualTo(Calculator.class);
            assertThat(seenArgs[0]).containsExactly(4, 5);
        }
    }

    @Nested
    @DisplayName("3. Exceptions")
    class Exceptions {

        @Test
        @DisplayName("an unwrapping handler lets the caller see the real ArithmeticException")
        void unwrapped() {
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThatThrownBy(() -> calc.divide(1, 0)).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("a handler that forgets to unwrap yields UndeclaredThrowableException")
        void naive() {
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    new NaiveHandler(new SimpleCalculator()));

            Throwable thrown = catchThrowable(() -> calc.divide(1, 0));

            assertThat(thrown).isInstanceOf(UndeclaredThrowableException.class);
            assertThat(thrown.getCause()).isInstanceOf(InvocationTargetException.class);
            assertThat(thrown.getCause().getCause()).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("normal calls work with the naive handler — only failures expose the flaw")
        void naiveWorksOnSuccess() {
            Calculator calc = DynamicProxyDemo.proxyOf(Calculator.class,
                    new NaiveHandler(new SimpleCalculator()));

            assertThat(calc.add(1, 1)).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("4. Methods of Object")
    class ObjectMethods {

        @Test
        @DisplayName("toString and hashCode are delegated to the target")
        void toStringAndHashCode() {
            SimpleCalculator target = new SimpleCalculator();
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class, new RecordingHandler(target));

            assertThat(proxy.toString()).isEqualTo(target.toString());
            assertThat(proxy.hashCode()).isEqualTo(target.hashCode());
        }

        @Test
        @DisplayName("proxy.equals(proxy) is false when equals is naively delegated")
        void equalsTrap() {
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class,
                    new RecordingHandler(new SimpleCalculator()));

            assertThat(proxy.equals(proxy)).isFalse();
        }

        @Test
        @DisplayName("the handler is invoked for toString, hashCode and equals")
        void handlerSeesObjectMethods() {
            RecordingHandler handler = new RecordingHandler(new SimpleCalculator());
            Calculator proxy = DynamicProxyDemo.proxyOf(Calculator.class, handler);

            proxy.toString();
            proxy.hashCode();
            proxy.equals(proxy);

            assertThat(handler.calls()).containsExactly("toString()", "hashCode()", "equals(<proxy>)");
        }
    }

    @Nested
    @DisplayName("5. Annotation-driven proxy")
    class AnnotationGuard {

        @Test
        @DisplayName("unprotected methods work for every role")
        void unprotectedMethod() {
            UserService asUser = DynamicProxyDemo.proxyOf(UserService.class,
                    new AccessGuardHandler(new InMemoryUserService(), "USER"));

            assertThat(asUser.findName(7)).isEqualTo("user-7");
        }

        @Test
        @DisplayName("a protected method is blocked for the wrong role and never reaches the target")
        void blocked() {
            InMemoryUserService real = new InMemoryUserService();
            UserService asUser = DynamicProxyDemo.proxyOf(UserService.class, new AccessGuardHandler(real, "USER"));

            assertThatThrownBy(() -> asUser.delete(7))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("ADMIN");
            assertThat(real.deletedIds()).isEmpty();
        }

        @Test
        @DisplayName("a protected method is allowed for the required role")
        void allowed() {
            InMemoryUserService real = new InMemoryUserService();
            UserService asAdmin = DynamicProxyDemo.proxyOf(UserService.class, new AccessGuardHandler(real, "ADMIN"));

            asAdmin.delete(7);

            assertThat(real.deletedIds()).containsExactly(7);
        }

        @Test
        @DisplayName("role comparison is exact (case-sensitive)")
        void caseSensitive() {
            InMemoryUserService real = new InMemoryUserService();
            UserService wrongCase = DynamicProxyDemo.proxyOf(UserService.class, new AccessGuardHandler(real, "admin"));

            assertThatThrownBy(() -> wrongCase.delete(1)).isInstanceOf(SecurityException.class);
        }

        @Test
        @DisplayName("the annotation lives on the interface method, not on the implementation's method")
        void annotationOnInterfaceOnly() throws Exception {
            Method onInterface = UserService.class.getMethod("delete", int.class);
            Method onImplementation = InMemoryUserService.class.getMethod("delete", int.class);

            assertThat(onInterface.isAnnotationPresent(RequiresRole.class)).isTrue();
            assertThat(onInterface.getAnnotation(RequiresRole.class).value()).isEqualTo("ADMIN");
            assertThat(onImplementation.isAnnotationPresent(RequiresRole.class)).isFalse();
        }
    }
}
