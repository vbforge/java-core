package com.vbforge.practice.practice_3_lambda_variable_capture;

/**
 * Instance fields are reached through the captured {@code this} reference. Because
 * {@code this} itself is effectively final, a field can be read AND mutated around the
 * lambda without the "effectively final" restriction that applies to locals.
 * (Static fields are accessed through the class, with no captured reference at all.)
 */
public class InstanceFieldCaptureDemo {

    private String label = "InstanceFieldCaptureDemo";

    void instanceFieldCapture() {
        // 'label' is an instance field: accessed via the implicit 'this'
        Runnable printer = () -> System.out.println("label = " + label);
        printer.run();

        label = "Changed!";        // mutating the field is allowed
        printer.run();             // the lambda sees the new value
    }

    public static void main(String[] args) {
        new InstanceFieldCaptureDemo().instanceFieldCapture();
    }
}
