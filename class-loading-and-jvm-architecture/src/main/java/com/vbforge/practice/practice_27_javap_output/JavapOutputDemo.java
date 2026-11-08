package com.vbforge.practice.practice_27_javap_output;

import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs the real tool: javap -c -p -cp (classpath) SampleMath, and prints the instructions of
 * two methods. Compare them with the output of practice_25. javap ships with the JDK, so on a
 * JRE-only install the demo only says it cannot find it.
 */
public class JavapOutputDemo {

    public static void main(String[] args) throws Exception {
        File javap = findJavap();
        if (javap == null) {
            System.out.println("javap not found next to this JVM (JRE-only install?), nothing to show");
            return;
        }

        List<String> command = Arrays.asList(javap.getPath(), "-c", "-p",
                "-cp", System.getProperty("java.class.path"), SampleMath.class.getName());
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

        printMethod(lines, "public int add(int, int);");
        printMethod(lines, "public static int square(int);");
    }

    private static File findJavap() {
        String home = System.getProperty("java.home");
        File[] candidates = {
                new File(home, "bin/javap"),
                new File(home, "../bin/javap")      // Java 8: java.home is the jre/ folder
        };
        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }

    private static void printMethod(List<String> lines, String signature) {
        boolean inside = false;
        for (String line : lines) {
            if (line.trim().equals(signature)) {
                inside = true;
            }
            if (inside) {
                if (line.trim().isEmpty()) {
                    return;
                }
                System.out.println(line.trim());
            }
        }
        System.out.println("(method not found in the javap output: " + signature + ")");
    }
}
