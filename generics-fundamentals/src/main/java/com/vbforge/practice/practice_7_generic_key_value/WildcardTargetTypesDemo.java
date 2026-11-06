package com.vbforge.practice.practice_7_generic_key_value;

/**
 * Which parameterizations can be assigned to a wildcard-typed variable:
 * {@code ? extends Number} takes Number and its subtypes,
 * {@code ? super Integer} takes Integer and its supertypes, {@code ?} takes anything.
 * What you can READ back is limited by the wildcard too.
 */
public class WildcardTargetTypesDemo {

    public static void main(String[] args) {

        // ================== Scene 1: upper bound - ? extends Number ==================
        KeyValue<? extends Number, ?> kv = new KeyValueImpl<Integer, Character>(12, 'k');
        System.out.println("extends: " + describe(kv));
        kv = new KeyValueImpl<Integer, String>(99, "bbb");
        System.out.println("extends: " + describe(kv));
        kv = new KeyValueImpl<Double, String>(9.99, "bbb");
        System.out.println("extends: " + describe(kv));
        // kv = new KeyValueImpl<String, Long>("x", 1L);   // compile error: the key type must be Number or its subclass
        Number key = kv.getKey();   // reads come back as the bound (Number); the value side is "?" -> Object
        System.out.println("read as Number: " + key.intValue());

        // ================== Scene 2: lower bound - ? super Integer ==================
        KeyValue<? super Integer, ?> kv2 = new KeyValueImpl<Integer, Character>(12, 'k');
        System.out.println("super:   " + describe(kv2));
        kv2 = new KeyValueImpl<Number, String>(99, "bbb");
        System.out.println("super:   " + describe(kv2));
        kv2 = new KeyValueImpl<Object, String>(9.99, "bbb");   // Object is a supertype of Integer; it can hold a Double
        System.out.println("super:   " + describe(kv2));
        // kv2 = new KeyValueImpl<Double, String>(9.99, "bbb"); // compile error: Double is not a supertype of Integer
        Object key2 = kv2.getKey();  // reads come back as Object only
        System.out.println("read as Object: " + key2);
    }

    // the unbounded wildcard on both sides: anything goes in, Object comes out
    private static String describe(KeyValue<?, ?> kv) {
        return "key=" + kv.getKey() + " (" + kv.getKey().getClass().getSimpleName() + "), value=" + kv.getValue();
    }
}
