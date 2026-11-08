package com.vbforge.practice.practice_18_heap_and_frame_lifetime;

import java.util.Arrays;

/**
 * A frame (and its locals) is gone when the method returns; an object on the heap lives as long
 * as something references it. The array created in the callee survives its frame, and every
 * call creates a new heap object.
 */
public class ObjectOutlivesItsFrameDemo {

    static int[] createInCallee() {
        int[] local = {1, 2, 3};
        return local;
    }

    public static void main(String[] args) {
        int[] kept = createInCallee();
        kept[0] = 99;
        System.out.println("object created in a finished frame is still usable: " + Arrays.toString(kept));

        int[] first = createInCallee();
        int[] second = createInCallee();
        System.out.println("two calls create two different heap objects: " + (first != second));
    }
}
