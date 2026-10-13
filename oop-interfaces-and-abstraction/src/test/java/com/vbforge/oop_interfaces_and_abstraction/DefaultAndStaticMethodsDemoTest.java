package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.defaults.Animal;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Bird;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Duck;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Flyer;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Gull;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Logger;
import com.vbforge.oop_interfaces_and_abstraction.defaults.PlainLogger;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Seabird;
import com.vbforge.oop_interfaces_and_abstraction.defaults.ShoutingLogger;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Swimmer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DefaultAndStaticMethodsDemoTest — verifies every concept shown in {@link DefaultAndStaticMethodsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * default, static and private interface methods.
 */
@DisplayName("DefaultAndStaticMethodsDemo — API contracts")
class DefaultAndStaticMethodsDemoTest {

    // ── 1. Why default methods exist ──────────────────────────────────────────

    @Nested
    @DisplayName("1. Why default methods exist")
    class EvolutionTest {

        @Test
        @DisplayName("an implementer that writes only log() still gets warn() and error()")
        void defaultsAreInherited() {
            assertThat(DefaultAndStaticMethodsDemo.plainLoggerLines())
                    .containsExactly("[WARN] disk", "[ERROR] fire");
        }

        @Test
        @DisplayName("PlainLogger declares no warn() or error() of its own")
        void notDeclaredInClass() throws NoSuchMethodException {
            assertThat(PlainLogger.class.getMethod("warn", String.class).getDeclaringClass())
                    .isEqualTo(Logger.class);
            assertThat(PlainLogger.class.getMethod("error", String.class).getDeclaringClass())
                    .isEqualTo(Logger.class);
        }

        @Test
        @DisplayName("Collection.removeIf is a default method of the JDK")
        void removeIf() {
            assertThat(DefaultAndStaticMethodsDemo.removeEvens(Arrays.asList(1, 2, 3, 4)))
                    .containsExactly(1, 3);
        }

        @Test
        @DisplayName("removeIf works on a copy and leaves the input list intact")
        void removeIfOnCopy() {
            List<Integer> input = Arrays.asList(1, 2, 3, 4);
            DefaultAndStaticMethodsDemo.removeEvens(input);
            assertThat(input).containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("Map.getOrDefault is a default method of the JDK")
        void getOrDefault() {
            assertThat(DefaultAndStaticMethodsDemo.countOrZero("missing")).isZero();
            assertThat(DefaultAndStaticMethodsDemo.countOrZero("known")).isEqualTo(5);
        }

        @Test
        @DisplayName("Iterable.forEach is a default method")
        void iterableForEach() throws NoSuchMethodException {
            assertThat(Iterable.class.getMethod("forEach", java.util.function.Consumer.class).isDefault()).isTrue();
        }
    }

    // ── 2. Overriding a default ───────────────────────────────────────────────

    @Nested
    @DisplayName("2. Overriding a default")
    class OverridingDefaultTest {

        @Test
        @DisplayName("an overridden default can reuse the original through Interface.super")
        void superCall() {
            assertThat(DefaultAndStaticMethodsDemo.shoutingLoggerLines())
                    .containsExactly("[WARN] DISK", "[ERROR] disk");
        }

        @Test
        @DisplayName("the override is declared in the class, the other default is not")
        void declaringClasses() throws NoSuchMethodException {
            assertThat(ShoutingLogger.class.getMethod("warn", String.class).getDeclaringClass())
                    .isEqualTo(ShoutingLogger.class);
            assertThat(ShoutingLogger.class.getMethod("error", String.class).getDeclaringClass())
                    .isEqualTo(Logger.class);
        }

        @Test
        @DisplayName("the override is chosen at runtime through the interface type")
        void dynamicDispatch() {
            ShoutingLogger shouting = new ShoutingLogger();
            Logger logger = shouting;
            logger.warn("x");
            assertThat(shouting.lines()).containsExactly("[WARN] X");
        }
    }

    // ── 3. Static methods in interfaces ───────────────────────────────────────

    @Nested
    @DisplayName("3. Static methods in interfaces")
    class StaticMethodsTest {

        @Test
        @DisplayName("a static interface method is called on the interface name")
        void calledOnInterface() {
            assertThat(Logger.prefix("INFO")).isEqualTo("[INFO] ");
        }

        @Test
        @DisplayName("a static factory returns a ready-made implementation")
        void staticFactory() {
            Logger silent = Logger.silent();
            silent.log("nobody hears this");
            silent.warn("nor this");
            assertThat(silent).isNotNull();
        }

        @Test
        @DisplayName("static interface methods are NOT inherited by implementers")
        void notInherited() {
            assertThat(DefaultAndStaticMethodsDemo.hasMethod(PlainLogger.class, "prefix")).isFalse();
            assertThat(DefaultAndStaticMethodsDemo.hasMethod(PlainLogger.class, "silent")).isFalse();
            assertThat(DefaultAndStaticMethodsDemo.hasMethod(PlainLogger.class, "warn")).isTrue();
        }

        @Test
        @DisplayName("Comparator.reverseOrder() builds a descending comparator")
        void reverseOrder() {
            assertThat(DefaultAndStaticMethodsDemo.sortedDescending(Arrays.asList(3, 1, 2)))
                    .containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("Comparator.comparing(key) sorts by a key and is stable")
        void comparing() {
            assertThat(DefaultAndStaticMethodsDemo.sortedByLength(Arrays.asList("ccc", "a", "bb")))
                    .containsExactly("a", "bb", "ccc");
            assertThat(DefaultAndStaticMethodsDemo.sortedByLength(Arrays.asList("bb", "aa", "c")))
                    .containsExactly("c", "bb", "aa");
        }
    }

    // ── 4. Private interface methods ──────────────────────────────────────────

    @Nested
    @DisplayName("4. Private interface methods (Java 9)")
    class PrivateMethodsTest {

        @Test
        @DisplayName("the interface declares exactly one private helper")
        void privateHelper() {
            assertThat(DefaultAndStaticMethodsDemo.privateMethodNames(Logger.class)).containsExactly("decorate");
        }

        @Test
        @DisplayName("the private helper is not part of the public API")
        void notPublic() {
            assertThat(DefaultAndStaticMethodsDemo.hasMethod(Logger.class, "decorate")).isFalse();
            assertThat(DefaultAndStaticMethodsDemo.hasMethod(PlainLogger.class, "decorate")).isFalse();
        }

        @Test
        @DisplayName("both defaults share the helper, so both decorate the same way")
        void sharedHelper() {
            PlainLogger logger = new PlainLogger();
            logger.warn("a");
            logger.error("a");
            assertThat(logger.lines()).containsExactly("[WARN] a", "[ERROR] a");
        }
    }

    // ── 5. Conflict rules ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Conflict rules")
    class ConflictRulesTest {

        @Test
        @DisplayName("rule 1: a class method beats an interface default")
        void classWins() {
            assertThat(new Bird().move()).isEqualTo("walk");
            assertThat(new Flyer() { }.move()).isEqualTo("fly");
        }

        @Test
        @DisplayName("the winning method is declared in the superclass")
        void classWinsDeclaringClass() throws NoSuchMethodException {
            assertThat(Bird.class.getMethod("move").getDeclaringClass()).isEqualTo(Animal.class);
        }

        @Test
        @DisplayName("rule 2: the more specific interface wins")
        void specificInterfaceWins() {
            assertThat(new Gull().move()).isEqualTo("glide");
        }

        @Test
        @DisplayName("Gull is both a Flyer and a Seabird")
        void gullTypes() {
            assertThat(new Gull()).isInstanceOf(Flyer.class).isInstanceOf(Seabird.class);
        }

        @Test
        @DisplayName("the winner is the same through any of the interface types")
        void dynamicDispatch() {
            Flyer asFlyer = new Gull();
            assertThat(asFlyer.move()).isEqualTo("glide");
        }
    }

    // ── 6. The diamond ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. The diamond")
    class DiamondTest {

        @Test
        @DisplayName("a class with two unrelated defaults must choose; Duck combines both")
        void explicitChoice() {
            assertThat(new Duck().move()).isEqualTo("fly & swim");
        }

        @Test
        @DisplayName("Duck has several types but only one move()")
        void oneMethod() throws NoSuchMethodException {
            assertThat(new Duck()).isInstanceOf(Flyer.class).isInstanceOf(Swimmer.class);
            assertThat(Duck.class.getMethod("move").getDeclaringClass()).isEqualTo(Duck.class);
        }

        @Test
        @DisplayName("each interface alone keeps its own default")
        void interfacesUnchanged() {
            assertThat(new Flyer() { }.move()).isEqualTo("fly");
            assertThat(new Swimmer() { }.move()).isEqualTo("swim");
        }

        @Test
        @DisplayName("the diamond is resolved per class: another class may choose differently")
        void anotherClassChoosesDifferently() {
            class Penguin implements Flyer, Swimmer {
                @Override
                public String move() {
                    return Swimmer.super.move();
                }
            }
            assertThat(new Penguin().move()).isEqualTo("swim");
        }
    }

    // ── 7. Gotchas ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("7. Gotchas")
    class GotchasTest {

        @Test
        @DisplayName("isDefault() separates default from abstract methods")
        void isDefault() {
            assertThat(DefaultAndStaticMethodsDemo.isDefault(Logger.class, "warn", String.class)).isTrue();
            assertThat(DefaultAndStaticMethodsDemo.isDefault(Logger.class, "error", String.class)).isTrue();
            assertThat(DefaultAndStaticMethodsDemo.isDefault(Logger.class, "log", String.class)).isFalse();
        }

        @Test
        @DisplayName("default methods are public")
        void defaultsArePublic() {
            assertThat(DefaultAndStaticMethodsDemo.isPublic(Logger.class, "warn", String.class)).isTrue();
        }

        @Test
        @DisplayName("an interface declares no instance fields")
        void noState() {
            assertThat(Logger.class.getDeclaredFields()).isEmpty();
        }

        @Test
        @DisplayName("an interface cannot override Object methods: toString still comes from the class")
        void objectMethodsStayWithClasses() {
            assertThat(new PlainLogger().toString()).startsWith(PlainLogger.class.getName() + "@");
        }
    }
}
