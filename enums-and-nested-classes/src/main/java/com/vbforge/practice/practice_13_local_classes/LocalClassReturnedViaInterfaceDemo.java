package com.vbforge.practice.practice_13_local_classes;

/**
 * The method dest() declares a local class PD that implements LabelReader, and returns it through the
 * interface type: the caller never learns how the label is formed. readLabel() joins the label, the
 * outer field y and the method parameter.
 */
public class LocalClassReturnedViaInterfaceDemo {

    public static void main(String[] args) {
        DemoLocal demoLocal = new DemoLocal();
        LabelReader read = demoLocal.dest("QQQQQ");
        System.out.println(read.readLabel());   // qqqqq33QQQQQ
    }
}

class DemoLocal {
    private int y = 33;

    public LabelReader dest(final String stringForLabel) {
        class PD implements LabelReader {
            private final String label;

            public PD(String label) {
                this.label = label;
            }

            @Override
            public String readLabel() {
                return label + y + stringForLabel;
            }
        }
        return new PD("qqqqq");
    }
}

interface LabelReader {
    String readLabel();
}
