package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo.ClassFileInfo;
import com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo.MethodInfo;
import com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo.SampleMath;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BytecodeDemo — API contracts")
class BytecodeDemoTest {

    private static ClassFileInfo sampleMath() throws IOException {
        return BytecodeDemo.parse(ClassLoaderDemo.readClassFile(SampleMath.class));
    }

    @Nested
    @DisplayName("1. Header and constant pool")
    class HeaderTest {

        @Test
        @DisplayName("Every class file starts with the magic number 0xCAFEBABE")
        void magic() throws Exception {
            assertThat(sampleMath().magic).isEqualTo(0xCAFEBABEL);
        }

        @Test
        @DisplayName("The major version is at least 52 (Java 8)")
        void majorVersion() throws Exception {
            assertThat(sampleMath().majorVersion).isGreaterThanOrEqualTo(52);
        }

        @Test
        @DisplayName("The string constant is stored as a UTF-8 entry in the constant pool")
        void literalInPool() throws Exception {
            assertThat(sampleMath().utf8Constants).contains(SampleMath.LITERAL);
        }

        @Test
        @DisplayName("Method names and descriptors are constant-pool entries too")
        void namesAndDescriptorsInPool() throws Exception {
            assertThat(sampleMath().utf8Constants)
                    .contains("add", "square", "(II)I", "(I)I", "<init>", "()V", "Code");
        }

        @Test
        @DisplayName("constant_pool_count is larger than the number of UTF-8 entries (index 0 is unused)")
        void poolCountIncludesUnusedSlot() throws Exception {
            ClassFileInfo info = sampleMath();

            assertThat(info.constantPoolCount).isGreaterThan(info.utf8Constants.size());
        }

        @Test
        @DisplayName("The file records one method per declared method plus the default constructor")
        void methodCount() throws Exception {
            assertThat(sampleMath().methods).hasSize(3);
        }

        @Test
        @DisplayName("Truncated input is reported as an IOException")
        void truncated() {
            assertThatThrownBy(() -> BytecodeDemo.parse(new byte[] {1, 2}))
                    .isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("2. Methods and the Code attribute")
    class CodeTest {

        @Test
        @DisplayName("add(int,int) is: iload_1, iload_2, iadd, ireturn")
        void addBytes() throws Exception {
            MethodInfo add = sampleMath().method("add", "(II)I");

            assertThat(add.code).isEqualTo(new byte[] {0x1B, 0x1C, 0x60, (byte) 0xAC});
        }

        @Test
        @DisplayName("square(int) is: iload_0, iload_0, imul, ireturn")
        void squareBytes() throws Exception {
            MethodInfo square = sampleMath().method("square", "(I)I");

            assertThat(square.code).isEqualTo(new byte[] {0x1A, 0x1A, 0x68, (byte) 0xAC});
        }

        @Test
        @DisplayName("mnemonics() decodes the opcodes the way javap -c prints them")
        void mnemonicsDecoded() throws Exception {
            ClassFileInfo info = sampleMath();

            assertThat(BytecodeDemo.mnemonics(info.method("add", "(II)I").code))
                    .containsExactly("iload_1", "iload_2", "iadd", "ireturn");
            assertThat(BytecodeDemo.mnemonics(info.method("square", "(I)I").code))
                    .containsExactly("iload_0", "iload_0", "imul", "ireturn");
        }

        @Test
        @DisplayName("Both arithmetic methods need an operand stack of depth 2")
        void maxStack() throws Exception {
            ClassFileInfo info = sampleMath();

            assertThat(info.method("add", "(II)I").maxStack).isEqualTo(2);
            assertThat(info.method("square", "(I)I").maxStack).isEqualTo(2);
        }

        @Test
        @DisplayName("Looking up a method that does not exist returns null")
        void missingMethod() throws Exception {
            assertThat(sampleMath().method("subtract", "(II)I")).isNull();
        }

        @Test
        @DisplayName("Unknown opcodes are printed as hex")
        void unknownOpcode() {
            assertThat(BytecodeDemo.mnemonics(new byte[] {(byte) 0xFF})).containsExactly("0xff");
        }
    }

    @Nested
    @DisplayName("3. Local-variable slots")
    class SlotsTest {

        @Test
        @DisplayName("An instance method reads its arguments from slots 1 and 2 (slot 0 is this)")
        void instanceMethodStartsAtSlotOne() throws Exception {
            MethodInfo add = sampleMath().method("add", "(II)I");

            assertThat(BytecodeDemo.mnemonics(add.code)).startsWith("iload_1", "iload_2");
        }

        @Test
        @DisplayName("A static method reads its argument from slot 0")
        void staticMethodStartsAtSlotZero() throws Exception {
            MethodInfo square = sampleMath().method("square", "(I)I");

            assertThat(BytecodeDemo.mnemonics(square.code)).startsWith("iload_0");
        }

        @Test
        @DisplayName("max_locals is 3 for the instance method and 1 for the static method")
        void maxLocals() throws Exception {
            ClassFileInfo info = sampleMath();

            assertThat(info.method("add", "(II)I").maxLocals).isEqualTo(3);
            assertThat(info.method("square", "(I)I").maxLocals).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("4. The default constructor")
    class ConstructorTest {

        @Test
        @DisplayName("A class without a declared constructor still has <init>()V")
        void constructorExists() throws Exception {
            assertThat(sampleMath().method("<init>", "()V")).isNotNull();
        }

        @Test
        @DisplayName("Its body is aload_0, invokespecial, return")
        void constructorBody() throws Exception {
            MethodInfo init = sampleMath().method("<init>", "()V");

            assertThat(BytecodeDemo.mnemonics(init.code)).containsExactly("aload_0", "invokespecial", "return");
        }

        @Test
        @DisplayName("It is five bytes long: 1 + 3 (opcode and 2-byte operand) + 1")
        void constructorLength() throws Exception {
            MethodInfo init = sampleMath().method("<init>", "()V");

            assertThat(init.code).hasSize(5);
            assertThat(init.code).startsWith((byte) 0x2A, (byte) 0xB7).endsWith((byte) 0xB1);
        }

        @Test
        @DisplayName("It needs one stack slot and one local slot (this)")
        void constructorLimits() throws Exception {
            MethodInfo init = sampleMath().method("<init>", "()V");

            assertThat(init.maxStack).isEqualTo(1);
            assertThat(init.maxLocals).isEqualTo(1);
        }
    }
}
