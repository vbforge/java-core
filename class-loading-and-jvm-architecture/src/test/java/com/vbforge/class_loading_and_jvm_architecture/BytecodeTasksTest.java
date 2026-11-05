package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("BytecodeTasks — your implementations")
class BytecodeTasksTest {

    private BytecodeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BytecodeTasks();
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement hasValidMagic()")
    @DisplayName("hasValidMagic")
    class HasValidMagicTest {

        @Test
        @DisplayName("Accepts CA FE BA BE followed by more bytes")
        void valid() {
            assertThat(tasks.hasValidMagic(bytes(0xCA, 0xFE, 0xBA, 0xBE, 0, 0, 0, 52))).isTrue();
        }

        @Test
        @DisplayName("Accepts exactly the four magic bytes")
        void exactlyFour() {
            assertThat(tasks.hasValidMagic(bytes(0xCA, 0xFE, 0xBA, 0xBE))).isTrue();
        }

        @Test
        @DisplayName("Rejects arrays shorter than four bytes")
        void tooShort() {
            assertThat(tasks.hasValidMagic(bytes(0xCA, 0xFE, 0xBA))).isFalse();
            assertThat(tasks.hasValidMagic(new byte[0])).isFalse();
        }

        @Test
        @DisplayName("Rejects a wrong first byte")
        void wrongFirstByte() {
            assertThat(tasks.hasValidMagic(bytes(0x00, 0xFE, 0xBA, 0xBE))).isFalse();
        }

        @Test
        @DisplayName("Rejects a wrong last byte")
        void wrongLastByte() {
            assertThat(tasks.hasValidMagic(bytes(0xCA, 0xFE, 0xBA, 0xBF))).isFalse();
        }

        @Test
        @DisplayName("Rejects null")
        void nullInput() {
            assertThat(tasks.hasValidMagic(null)).isFalse();
        }

        @Test
        @DisplayName("Accepts a real class file")
        void realClassFile() throws Exception {
            assertThat(tasks.hasValidMagic(ClassLoaderDemo.readClassFile(BytecodeTasks.class))).isTrue();
        }

        @Test
        @DisplayName("Rejects a real class file with a patched magic number")
        void patchedClassFile() throws Exception {
            byte[] patched = ClassLifecycleDemo.withBrokenMagic(ClassLoaderDemo.readClassFile(BytecodeTasks.class));

            assertThat(tasks.hasValidMagic(patched)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement javaReleaseFor()")
    @DisplayName("javaReleaseFor")
    class JavaReleaseForTest {

        @ParameterizedTest(name = "major {0} → Java {1}")
        @CsvSource({"49,5", "50,6", "52,8", "53,9", "55,11", "61,17", "65,21"})
        @DisplayName("Maps major versions to Java releases")
        void mapsVersions(int major, int release) {
            assertThat(tasks.javaReleaseFor(major)).isEqualTo(release);
        }

        @ParameterizedTest(name = "major {0} is rejected")
        @ValueSource(ints = {48, 45, 0, -1})
        @DisplayName("Rejects versions below 49")
        void rejectsOldVersions(int major) {
            assertThatThrownBy(() -> tasks.javaReleaseFor(major)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Agrees with the major version of a real class file")
        void realClassFile() throws Exception {
            int major = BytecodeDemo.parse(ClassLoaderDemo.readClassFile(BytecodeTasks.class)).majorVersion;

            assertThat(tasks.javaReleaseFor(major)).isGreaterThanOrEqualTo(8);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement localSlotsFor()")
    @DisplayName("localSlotsFor")
    class LocalSlotsForTest {

        @Test
        @DisplayName("Instance method (int,int): this + 2 slots")
        void instanceTwoInts() {
            assertThat(tasks.localSlotsFor("(II)I", false)).isEqualTo(3);
        }

        @Test
        @DisplayName("Static method (int,int): no this")
        void staticTwoInts() {
            assertThat(tasks.localSlotsFor("(II)I", true)).isEqualTo(2);
        }

        @ParameterizedTest(name = "{0} static={1} → {2} slots")
        @CsvSource({
            "'()V',true,0",
            "'()V',false,1",
            "'(JD)V',true,4",
            "'(JJJ)J',true,6",
            "'(Ljava/lang/String;[IJ)V',false,5",
            "'([Ljava/lang/String;)V',true,1",
            "'([[JLjava/util/List;)V',true,2",
            "'(ZBCSIF)V',true,6"
        })
        @DisplayName("Counts slots for assorted descriptors")
        void assortedDescriptors(String descriptor, boolean isStatic, int expected) {
            assertThat(tasks.localSlotsFor(descriptor, isStatic)).isEqualTo(expected);
        }

        @Test
        @DisplayName("The return type does not change the result")
        void returnTypeIgnored() {
            assertThat(tasks.localSlotsFor("(I)J", true)).isEqualTo(tasks.localSlotsFor("(I)V", true));
        }

        @Test
        @DisplayName("Matches max_locals of real methods whose bodies use only their parameters")
        void matchesRealMethods() throws Exception {
            BytecodeDemo.ClassFileInfo info =
                    BytecodeDemo.parse(ClassLoaderDemo.readClassFile(BytecodeDemo.SampleMath.class));

            assertThat(tasks.localSlotsFor("(II)I", false)).isEqualTo(info.method("add", "(II)I").maxLocals);
            assertThat(tasks.localSlotsFor("(I)I", true)).isEqualTo(info.method("square", "(I)I").maxLocals);
        }
    }
}
