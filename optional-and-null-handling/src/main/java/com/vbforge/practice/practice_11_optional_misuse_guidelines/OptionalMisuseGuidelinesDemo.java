package com.vbforge.practice.practice_11_optional_misuse_guidelines;

/**
 * Where NOT to use Optional. Printed reminders only; the bad code is kept as comments.
 *
 * (get() without a check and orElse with an expensive argument are practiced with real
 * code in practice_2, practice_3 and practice_10.)
 */
public class OptionalMisuseGuidelinesDemo {

    public static void main(String[] args) {
        // Misuse 1: Optional.ofNullable(x).isPresent() instead of x != null
        String value = "hello";
        // bad:  Optional.ofNullable(value).isPresent()
        // good: value != null
        System.out.println("plain null check: " + (value != null));
        System.out.println("1: use a null check, not Optional.ofNullable(...).isPresent()");

        // Misuse 2: Optional as a field type
        // private Optional<String> name;   <- don't do this
        System.out.println("2: don't use Optional as a field (not Serializable, overhead)");

        // Misuse 3: Optional as a method parameter
        // void process(Optional<String> name)   <- don't do this; use overloads or @Nullable
        System.out.println("3: don't pass Optional as a method parameter");
    }
}
