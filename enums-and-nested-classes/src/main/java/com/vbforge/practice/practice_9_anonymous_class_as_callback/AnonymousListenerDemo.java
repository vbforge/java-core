package com.vbforge.practice.practice_9_anonymous_class_as_callback;

/** An anonymous listener stored in a field of the Button and invoked later (callback). */
public class AnonymousListenerDemo {

    public static void main(String[] args) {
        Button button = new Button();

        button.setClickListener(new ClickListener() {
            @Override
            public void onClick(String message) {
                System.out.println(message);
            }
        });

        button.click();   // Button clicked!
    }
}

class Button {

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void click() {
        if (clickListener != null) {
            clickListener.onClick("Button clicked!");
        }
    }
}

interface ClickListener {
    void onClick(String message);
}
