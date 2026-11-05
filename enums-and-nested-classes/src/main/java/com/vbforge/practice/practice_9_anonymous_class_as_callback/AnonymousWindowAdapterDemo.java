package com.vbforge.practice.practice_9_anonymous_class_as_callback;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Anonymous subclass of an adapter class: WindowAdapter has an empty implementation of every window
 * listener method, so only the one that matters (windowClosing) is overridden.
 *
 * In a real GUI the adapter is attached with {@code frame.addWindowListener(adapter)} and
 * windowClosing calls {@code System.exit(0)}. Here the handler is invoked directly (no window is
 * opened) and prints instead of exiting, so the demo also runs headless.
 */
public class AnonymousWindowAdapterDemo {

    public static void main(String[] args) {
        WindowAdapter closingHandler = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("windowClosing handled (a real GUI would call System.exit(0))");
            }
        };

        closingHandler.windowOpened(null);    // inherited empty method: nothing happens
        closingHandler.windowClosing(null);   // overridden method
    }
}
