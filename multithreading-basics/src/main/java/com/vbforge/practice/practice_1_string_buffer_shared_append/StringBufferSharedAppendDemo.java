package com.vbforge.practice.practice_1_string_buffer_shared_append;

import java.util.Arrays;

/**
 * Several threads append to one shared StringBuffer without any extra locking.
 * Each append(String) is synchronized, so no line is lost or torn; the ORDER of lines is not
 * deterministic, so the demo prints only order-independent facts (a plain StringBuilder gives no such guarantee).
 */
public class StringBufferSharedAppendDemo {

    private static final int REPEATS = 1000;

    public static void main(String[] args) throws InterruptedException {
        StringBuffer shared = new StringBuffer();

        Thread t1 = new Thread(new StringModifier(shared, "Thread #1: "));
        Thread t2 = new Thread(new StringModifier(shared, "Thread #2: "));
        Thread t3 = new Thread(new StringModifier(shared, "Thread #3: "));
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        String[] lines = shared.toString().split("\n");
        System.out.println("total lines:    " + lines.length);
        for (int id = 1; id <= 3; id++) {
            String prefix = "Thread #" + id + ": ";
            System.out.println(prefix + "lines = " + Arrays.stream(lines).filter(l -> l.equals(prefix)).count());
        }
        System.out.println("all lines intact: " + Arrays.stream(lines).allMatch(l -> l.matches("Thread #[123]: ")));
    }

    static class StringModifier implements Runnable {
        private final StringBuffer stringBuffer;
        private final String message;

        StringModifier(StringBuffer stringBuffer, String message) {
            this.stringBuffer = stringBuffer;
            this.message = message;
        }

        @Override
        public void run() {
            for (int i = 0; i < REPEATS; i++) {
                stringBuffer.append(message + "\n"); // one synchronized append per whole line
            }
        }
    }
}
