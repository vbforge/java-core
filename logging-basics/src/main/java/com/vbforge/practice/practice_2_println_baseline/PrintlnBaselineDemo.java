package com.vbforge.practice.practice_2_println_baseline;

import com.vbforge.practice.practice_1_person_sample_data.PersonRecord;

/**
 * The "before" picture: the same flow as the logging practice classes, reported with
 * System.out.println only. Output has no level, no timestamp, no logger name, and it cannot
 * be switched off or redirected by configuration - the reasons to use a logging framework.
 */
public class PrintlnBaselineDemo {

    public static void main(String[] args) {
        // Create a PersonRecord instance (immutable)
        PersonRecord johnRecord = new PersonRecord("John", 30);
        System.out.println("---------Using PersonRecord:---------");
        System.out.println(johnRecord);
        System.out.println(johnRecord.fullDescription());
        System.out.println("Is John an adult? " + johnRecord.isAdult());

        // Create another PersonRecord instance
        PersonRecord janeRecord = new PersonRecord("Jane", 17);
        System.out.println("Using PersonRecord:");
        System.out.println(janeRecord);
        System.out.println(janeRecord.fullDescription());
        System.out.println("Is Jane an adult? " + janeRecord.isAdult());
        System.out.println(PersonRecord.compareAges(johnRecord, janeRecord));
    }
}
