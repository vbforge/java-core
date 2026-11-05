package com.vbforge.practice.practice_2_enum_fields_constructors_and_methods;

/** Enum with a per-constant int field (statusCode) set through the constructor and read through a getter. */
public class EnumFieldAndGetterDemo {

    public static void main(String[] args) {
        for (DocumentStatus docSt : DocumentStatus.values()) {
            System.out.println("Name = " + docSt.name() +
                    ", statusCode is = " + docSt.getStatusCode());
        }
    }
}

enum DocumentStatus {

    NEW(31),
    DRAFT(23),
    PUBLISHED(52),
    ARCHIVED(77);

    private final int statusCode;

    DocumentStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
