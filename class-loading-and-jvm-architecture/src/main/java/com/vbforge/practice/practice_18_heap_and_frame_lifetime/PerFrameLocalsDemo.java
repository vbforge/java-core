package com.vbforge.practice.practice_18_heap_and_frame_lifetime;

/**
 * Locals live in the frame, so every recursive call has its own copy. Deeper calls
 * cannot overwrite the local of the caller: the values come back intact while unwinding.
 */
public class PerFrameLocalsDemo {

    static void countdown(int n) {
        int mine = n * 10;
        if (n > 1) {
            countdown(n - 1);
        }
        System.out.println("unwinding n=" + n + ", my own local = " + mine);
    }

    public static void main(String[] args) {
        countdown(4);
    }
}
