package com.vbforge.practice.practice_19_out_of_memory_errors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fixture: runs a class in a fresh JVM with its own flags (-Xmx, -XX:MaxMetaspaceSize) and
 * returns its output, so a demo can hit a memory limit on purpose without hurting itself.
 */
public final class ChildJvm {

    private ChildJvm() {
    }

    public static List<String> run(Class<?> mainClass, List<String> jvmOptions, String... args)
            throws IOException, InterruptedException {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        List<String> command = new ArrayList<>();
        command.add(java);
        command.addAll(jvmOptions);
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(mainClass.getName());
        command.addAll(Arrays.asList(args));

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Picked up ")) {
                    lines.add(line);
                }
            }
        }
        process.waitFor();
        return lines;
    }
}
