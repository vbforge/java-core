package com.vbforge.practice.practice_15_default_values_and_final;

/**
 * Default values of fields. Instance and static fields get defaults automatically;
 * local variables do NOT - they must be initialized before use.
 */
public class DefaultFieldValuesDemo {

    byte    defaultByte;
    short   defaultShort;
    int     defaultInt;
    long    defaultLong;
    float   defaultFloat;
    double  defaultDouble;
    char    defaultChar;
    boolean defaultBoolean;
    String  defaultReference;

    public static void main(String[] args) {
        DefaultFieldValuesDemo obj = new DefaultFieldValuesDemo();

        System.out.println("byte:     " + obj.defaultByte);
        System.out.println("short:    " + obj.defaultShort);
        System.out.println("int:      " + obj.defaultInt);
        System.out.println("long:     " + obj.defaultLong);
        System.out.println("float:    " + obj.defaultFloat);
        System.out.println("double:   " + obj.defaultDouble);
        System.out.println("char:     code " + (int) obj.defaultChar + " (\\u0000)");
        System.out.println("boolean:  " + obj.defaultBoolean);
        System.out.println("String:   " + obj.defaultReference);

        // int local;
        // System.out.println(local); // compile error: variable might not have been initialized
    }
}
