package com.vbforge.practice.practice_4_nested_if;

/**
 * if / else-if / else with progressively deeper nesting.
 *
 *   Level 1: simple chain (kid / adult / over 60)
 *   Level 2: nested check for the 10..15 sub-band
 *   Level 3: nested with an additional boolean (withParents) and several conditions
 *
 * Rule of thumb: if you need more than 2 levels of nesting, extract a helper or
 * flatten the condition. The last method is intentionally ugly - it shows why.
 */
public class AgeCategoryDemo {

    public static void main(String[] args) {
        System.out.println("--- simple chain ---");
        for (int age : new int[]{5, 15, 30, 65}) {
            System.out.println(age + " -> " + category(age));
        }

        System.out.println("\n--- with nested 10..15 sub-band ---");
        System.out.println(12 + " -> " + categoryNested(12));
        System.out.println(8 + " -> " + categoryNested(8));

        System.out.println("\n--- deep nested (withParents) ---");
        System.out.println(12 + ", with parents -> " + admission(12, true));
        System.out.println(12 + ", alone        -> " + admission(12, false));
        System.out.println(8 + ", alone        -> " + admission(8, false));
        System.out.println(18 + "               -> " + admission(18, false));
        System.out.println(19 + "               -> " + admission(19, false));
        System.out.println(55 + "               -> " + admission(55, false));
    }

    static String category(int age) {
        if (age < 18) return "Kid";
        else if (age < 60) return "Adult";
        else return "Over 60";
    }

    static String categoryNested(int age) {
        if (age < 18) {
            if (age >= 10 && age <= 15) return "Kid (10..15)";
            return "Kid";
        } else if (age < 60) {
            return "Adult";
        } else {
            return "Over 60";
        }
    }

    static String admission(int age, boolean withParents) {
        if (age < 18) {
            if (age >= 10 && age <= 15 && withParents) {
                return "Allowed with parents";
            } else if ((age == 7 || age == 9) && !withParents) {
                return "Free entry";
            }
            return "No admission";
        } else if (age == 18) {
            return "You are 18";
        } else if (age == 19) {
            return "You are 19";
        } else {
            return age > 50 ? "You are 50+" : "You are under 50";
        }
    }
}
