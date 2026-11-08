package com.vbforge.practice.practice_2_jit_introspection;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Which execution mode is this JVM in? Reads the JVM flags and java.vm.info.
 * Try: -Xint (interpreter only), -Xcomp (compile on first call),
 * -XX:TieredStopAtLevel=1 (C1 only), -XX:-TieredCompilation (C2 only).
 */
public class JvmExecutionModeDemo {

    public static void main(String[] args) {
        List<String> flags = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("JVM flags: " + flags);

        String mode;
        if (flags.contains("-Xint")) {
            mode = "interpreter only (-Xint)";
        } else if (flags.contains("-Xcomp")) {
            mode = "compile everything on first call (-Xcomp)";
        } else if (flags.contains("-XX:TieredStopAtLevel=1")) {
            mode = "C1 only (TieredStopAtLevel=1)";
        } else if (flags.contains("-XX:-TieredCompilation")) {
            mode = "C2 only, no tiers (-TieredCompilation)";
        } else {
            mode = "default: tiered, interpreter -> C1 -> C2";
        }
        System.out.println("execution mode: " + mode);
        System.out.println("java.vm.info: " + System.getProperty("java.vm.info"));
    }
}
