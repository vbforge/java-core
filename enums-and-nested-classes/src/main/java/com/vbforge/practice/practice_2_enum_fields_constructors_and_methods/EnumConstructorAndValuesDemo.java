package com.vbforge.practice.practice_2_enum_fields_constructors_and_methods;

/**
 * Enum with an explicit constructor + the built-in values() / name() API.
 * The constructor runs once per constant, when the enum class is initialised.
 */
public class EnumConstructorAndValuesDemo {

    public static void main(String[] args) {
        WorkDay x = WorkDay.MONDAY;     // first use triggers class init -> "constructor" x3
        int n = x.getN();
        System.out.println("n = " + n);

        WorkDay[] workDays = WorkDay.values();
        for (int i = 0; i < workDays.length; i++) {
            WorkDay item = workDays[i];
            System.out.println(item);
            String name = item.name();
            System.out.println("name = " + name);
        }
    }
}

enum WorkDay {

    MONDAY(1), TUESDAY(2), WEDNESDAY(3);

    private final int n;

    WorkDay(int n) {
        this.n = n;
        System.out.println("constructor");
    }

    public int getN() {
        return n;
    }
}
