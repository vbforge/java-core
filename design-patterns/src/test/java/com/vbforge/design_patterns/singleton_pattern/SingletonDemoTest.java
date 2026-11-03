package com.vbforge.design_patterns.singleton_pattern;

import com.vbforge.design_patterns.singleton_pattern.SingletonDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SingletonDemo — API contracts")
class SingletonDemoTest {

    @Nested
    @DisplayName("1. Eager singleton")
    class EagerTest {

        @Test
        @DisplayName("getInstance() returns the same reference on every call")
        void sameInstance() {
            assertThat(SingletonDemo.Eager.getInstance())
                    .isSameAs(SingletonDemo.Eager.getInstance());
        }

        @Test
        @DisplayName("value() exposes state stored on the shared instance")
        void sharedState() {
            assertThat(SingletonDemo.Eager.getInstance().value()).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("2. Lazy (unsafe)")
    class LazyTest {

        @Test
        @DisplayName("getInstance() returns the same reference on every call")
        void sameInstance() {
            assertThat(SingletonDemo.Lazy.getInstance())
                    .isSameAs(SingletonDemo.Lazy.getInstance());
        }
    }

    @Nested
    @DisplayName("3. Double-checked locking")
    class DclTest {

        @Test
        @DisplayName("getInstance() returns the same reference on every call")
        void sameInstance() {
            assertThat(SingletonDemo.Dcl.getInstance())
                    .isSameAs(SingletonDemo.Dcl.getInstance());
        }

        @Test
        @DisplayName("instance field is volatile — modifier check")
        void fieldIsVolatile() throws Exception {
            var f = SingletonDemo.Dcl.class.getDeclaredField("instance");
            assertThat(Modifier.isVolatile(f.getModifiers())).isTrue();
        }
    }

    @Nested
    @DisplayName("4. Holder idiom")
    class HolderTest {

        @Test
        @DisplayName("getInstance() returns the same reference on every call")
        void sameInstance() {
            assertThat(SingletonDemo.Holder.getInstance())
                    .isSameAs(SingletonDemo.Holder.getInstance());
        }
    }

    @Nested
    @DisplayName("5. Enum singleton")
    class EnumTest {

        @Test
        @DisplayName("enum constant is the single instance")
        void sameInstance() {
            assertThat(SingletonDemo.EnumS.INSTANCE).isSameAs(SingletonDemo.EnumS.INSTANCE);
        }

        @Test
        @DisplayName("reflection cannot create a second enum instance")
        void reflectionProof() throws Exception {
            Constructor<SingletonDemo.EnumS> c =
                    SingletonDemo.EnumS.class.getDeclaredConstructor(String.class, int.class);
            c.setAccessible(true);
            assertThatThrownBy(() -> c.newInstance("INSTANCE", 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("value() returns the constant's state")
        void value() {
            assertThat(SingletonDemo.EnumS.INSTANCE.value()).isEqualTo(7);
        }
    }
}