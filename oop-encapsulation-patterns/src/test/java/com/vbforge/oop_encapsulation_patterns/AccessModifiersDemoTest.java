package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Base;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Derived;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Greeter;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.MathUtil;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Meter;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.PackageNested;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.ProtectedNested;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.PublicNested;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Slugger;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AccessModifiersDemoTest — verifies every concept shown in {@link AccessModifiersDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how access modifiers behave.
 */
@DisplayName("AccessModifiersDemo — API contracts")
class AccessModifiersDemoTest {

    // ── 1. The four levels ────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The four levels")
    class FourLevelsTest {

        @ParameterizedTest(name = "field {0} → {1}")
        @CsvSource({
                "secret,       private",
                "packageLevel, package-private",
                "shared,       protected",
                "open,         public",
        })
        @DisplayName("each field reports its own level")
        void fieldLevels(String field, String expected) throws Exception {
            int modifiers = Visibility.class.getDeclaredField(field).getModifiers();
            assertThat(AccessModifiersDemo.levelOf(modifiers)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "method {0} → {1}")
        @CsvSource({
                "privateMethod,   private",
                "packageMethod,   package-private",
                "protectedMethod, protected",
                "publicMethod,    public",
        })
        @DisplayName("each method reports its own level")
        void methodLevels(String method, String expected) throws Exception {
            int modifiers = Visibility.class.getDeclaredMethod(method).getModifiers();
            assertThat(AccessModifiersDemo.levelOf(modifiers)).isEqualTo(expected);
        }

        @Test
        @DisplayName("package-private is the absence of any access modifier")
        void packagePrivateIsDefault() {
            assertThat(AccessModifiersDemo.levelOf(0)).isEqualTo("package-private");
        }
    }

    // ── 2. Access on classes ──────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Access on classes")
    class ClassLevelTest {

        @Test
        @DisplayName("a top-level class is public here")
        void topLevel() {
            assertThat(AccessModifiersDemo.levelOf(AccessModifiersDemo.class.getModifiers())).isEqualTo("public");
        }

        @Test
        @DisplayName("a nested class may be private")
        void privateNested() {
            assertThat(AccessModifiersDemo.levelOf(AccessModifiersDemo.hiddenHelperType().getModifiers()))
                    .isEqualTo("private");
        }

        @Test
        @DisplayName("a nested class may be package-private, protected or public")
        void otherNested() {
            assertThat(AccessModifiersDemo.levelOf(PackageNested.class.getModifiers())).isEqualTo("package-private");
            assertThat(AccessModifiersDemo.levelOf(ProtectedNested.class.getModifiers())).isEqualTo("protected");
            assertThat(AccessModifiersDemo.levelOf(PublicNested.class.getModifiers())).isEqualTo("public");
        }
    }

    // ── 3. private is per class ───────────────────────────────────────────────

    @Nested
    @DisplayName("3. private is per class")
    class PrivatePerClassTest {

        @Test
        @DisplayName("one instance reads another instance's private field")
        void readsOtherInstance() {
            assertThat(new Meter(10).sameReading(new Meter(10))).isTrue();
            assertThat(new Meter(10).sameReading(new Meter(99))).isFalse();
        }

        @Test
        @DisplayName("the enclosing class can read a nested class's private field")
        void nestedPrivilege() {
            assertThat(AccessModifiersDemo.directPeek(new Meter(99))).isEqualTo(99);
        }

        @Test
        @DisplayName("the field itself is private as far as reflection can tell")
        void fieldIsPrivate() throws Exception {
            int modifiers = Meter.class.getDeclaredField("reading").getModifiers();
            assertThat(AccessModifiersDemo.levelOf(modifiers)).isEqualTo("private");
        }
    }

    // ── 4. protected ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. protected")
    class ProtectedTest {

        @Test
        @DisplayName("the same package may call a protected method")
        void samePackage() {
            assertThat(new Base().id()).isEqualTo("base");
        }

        @Test
        @DisplayName("an override may widen protected to public")
        void wideningOverride() throws Exception {
            assertThat(AccessModifiersDemo.levelOf(Base.class.getDeclaredMethod("id").getModifiers()))
                    .isEqualTo("protected");
            assertThat(AccessModifiersDemo.levelOf(Derived.class.getDeclaredMethod("id").getModifiers()))
                    .isEqualTo("public");
        }

        @Test
        @DisplayName("a call through the parent type still reaches the override")
        void dynamicDispatch() {
            Base derived = new Derived();
            assertThat(derived.id()).isEqualTo("derived");
            assertThat(derived.label()).isEqualTo("[derived]");
        }
    }

    // ── 5. A small public surface ─────────────────────────────────────────────

    @Nested
    @DisplayName("5. A small public surface")
    class SmallSurfaceTest {

        @Test
        @DisplayName("the facade method does the whole job")
        void slugWorks() {
            assertThat(new Slugger().slug("  Hello, World!  ")).isEqualTo("hello-world");
            assertThat(new Slugger().slug("Java 17 -- Records")).isEqualTo("java-17-records");
        }

        @Test
        @DisplayName("only slug() is public")
        void onlyOnePublicMethod() {
            assertThat(AccessModifiersDemo.publicMethodNames(Slugger.class)).containsExactly("slug");
        }

        @Test
        @DisplayName("the helper steps are private")
        void helpersArePrivate() throws Exception {
            assertThat(AccessModifiersDemo.levelOf(
                    Slugger.class.getDeclaredMethod("normalize", String.class).getModifiers())).isEqualTo("private");
            assertThat(AccessModifiersDemo.levelOf(
                    Slugger.class.getDeclaredMethod("dashes", String.class).getModifiers())).isEqualTo("private");
        }
    }

    // ── 6. Gotchas ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Gotchas")
    class GotchasTest {

        @Test
        @DisplayName("interface methods are implicitly public")
        void interfaceMethodsArePublic() throws Exception {
            assertThat(AccessModifiersDemo.levelOf(Greeter.class.getMethod("hi").getModifiers())).isEqualTo("public");
        }

        @Test
        @DisplayName("a private constructor forbids new")
        void privateConstructor() {
            assertThat(MathUtil.class.getDeclaredConstructors()).hasSize(1);
            assertThat(AccessModifiersDemo.levelOf(MathUtil.class.getDeclaredConstructors()[0].getModifiers()))
                    .isEqualTo("private");
            assertThat(MathUtil.twice(21)).isEqualTo(42);
        }

        @Test
        @DisplayName("reflection can read a private field — modifiers are not security")
        void reflectionBypass() {
            assertThat(AccessModifiersDemo.peekWithReflection(new Meter(7))).isEqualTo(7);
        }
    }
}
