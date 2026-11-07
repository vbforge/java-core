package com.vbforge.practice.practice_8_spy_partial_mock;

/**
 * Spy + doReturn.
 * when(spy.get(5)).thenReturn("x") first CALLS spy.get(5) for real, so on an empty list it
 * throws IndexOutOfBoundsException before the stubbing is registered. doReturn(...) avoids that.
 */
public class SpyOverrideDemo {

    public static void main(String[] args) {
        SpyList spy = new SpyList();

        System.out.println("--- real methods still work ---");
        spy.add("a");
        System.out.println("size = " + spy.size() + ", get(0) = " + spy.get(0));

        System.out.println("--- when(spy.get(5)) style: the real method runs first ---");
        try {
            spy.get(5);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("  IndexOutOfBoundsException, the real get(5) was executed");
        }

        System.out.println("--- doReturn style: the real method is skipped ---");
        spy.doReturn(5, "x");
        System.out.println("get(5) = " + spy.get(5) + ", size still " + spy.size());
        System.out.println("get() was called " + spy.getCalls() + " times in total");
    }
}
