package com.vbforge.practice.set.practice_22_linkedhashset_object_contract;

import java.util.LinkedHashSet;

/**
 * The "what makes objects unique in a Set?" progression, using LinkedHashSet
 * so insertion order is preserved and the story is easy to read.
 *
 *   Scene 1 — Phone: NO equals, NO hashCode.
 *             Both phones fall back to Object identity ⇒ 2 entries.
 *
 *   Scene 2 — Smartphone: hashCode() based on price, but NO equals.
 *             Same hash, but equality is still identity ⇒ 2 entries.
 *             (Hash collision alone does NOT cause deduplication.)
 *
 *   Scene 3 — MyPhone: equals() AND hashCode() both based on price.
 *             Same price ⇒ same hash AND equal ⇒ 1 entry.
 *             Different price ⇒ 2 entries.
 *
 *   Scene 4 — upcast to Object, cast back to MyPhone: illustrates that
 *             the runtime class is what equals/hashCode operate on, not
 *             the declared reference type.
 */
public class LinkedHashSetContractProgression {

    public static void main(String[] args) {
        phoneWithoutContractScene();
        smartphoneWithHashCodeOnlyScene();
        myPhoneFullContractScene();
        upcastDemoScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — Phone: no equals, no hashCode → identity-based
    // ─────────────────────────────────────────────────────────────
    private static void phoneWithoutContractScene() {
        System.out.println("=== Scene 1: Phone (no equals, no hashCode) ===");

        LinkedHashSet<Phone> set = new LinkedHashSet<>();

        Phone lg = new Phone();
        lg.price=1000;

        Phone htc = new Phone();
        htc.price=1000;

        System.out.println("lg.hashCode="+lg.hashCode());
        System.out.println("htc.hashCode="+htc.hashCode());
        System.out.println("--------add--------------");

        set.add(lg);
        set.add(htc);

        System.out.println("set.size="+set.size());       // 2 — identity keeps both
        System.out.println("set.isEmpty()="+set.isEmpty());
        System.out.println("----------iterator for each--------");
        int step = 1;
        for(Object item : set){
            System.out.println(step+"="+item.toString());
            step++;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — Smartphone: hashCode only, no equals → still identity
    // ─────────────────────────────────────────────────────────────
    private static void smartphoneWithHashCodeOnlyScene() {
        System.out.println("\n=== Scene 2: Smartphone (hashCode only, no equals) ===");

        LinkedHashSet<Smartphone> set = new LinkedHashSet<>();
        Smartphone lg = new Smartphone();
        lg.price=1000;

        Smartphone htc = new Smartphone();
        htc.price=1000;

        System.out.println("lg.hashCode="+lg.hashCode());
        System.out.println("htc.hashCode="+htc.hashCode());
        boolean isEqual = lg.equals(htc);
        System.out.println("isEqual="+isEqual);            // false — Object.equals is identity
        System.out.println("--------add--------------");

        set.add(lg);
        set.add(htc);

        System.out.println("set.size="+set.size());       // 2 — same hash, but not equal
        System.out.println("set.isEmpty()="+set.isEmpty());
        System.out.println("----------iterator for each--------");
        int step = 0;
        for(Object item : set){
            System.out.println(step+"="+item.toString());
            step++;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — MyPhone: equals + hashCode both based on price → deduped
    // ─────────────────────────────────────────────────────────────
    private static void myPhoneFullContractScene() {
        System.out.println("\n=== Scene 3: MyPhone (equals + hashCode) ===");
        LinkedHashSet<MyPhone> set = new LinkedHashSet<>();
        MyPhone lg = new MyPhone();
        lg.price=1000;

        MyPhone htc = new MyPhone();
        htc.price=1000;

        System.out.println("lg.hashCode="+lg.hashCode());
        System.out.println("htc.hashCode="+htc.hashCode());
        boolean isEqual = lg.equals(htc);
        System.out.println("isEqual="+isEqual);            // true — both price-based
        System.out.println("--------add--------------");

        set.add(lg);
        set.add(htc);

        System.out.println("set.size="+set.size());       // 1 — deduped
        System.out.println("set.isEmpty()="+set.isEmpty());
        System.out.println("----------iterator for each--------");
        int step = 0;
        for(Object item : set){
            System.out.println(step+"="+item.toString());
            step++;
        }

        System.out.println("---------add item, but with other price--------------");
        MyPhone samsung = new MyPhone();
        samsung.price=2000;
        set.add(samsung);
        step = 0;
        for(Object item: set){
            System.out.println(step+"="+item.toString());
            step++;
        }
        System.out.println("set.size()="+set.size());      // 2 — different price, different identity
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — upcast/cast demo (identity is runtime class)
    // ─────────────────────────────────────────────────────────────
    private static void upcastDemoScene() {
        System.out.println("\n=== Scene 4: upcast to Object, cast back to MyPhone ===");
        MyPhone htc = new MyPhone();
        htc.price=1000;

        // IS-A; MyPhone IS-A Object
        // left side - parent reference, right side - child instance
        Object lg = new MyPhone();
        MyPhone lgPhone = (MyPhone) lg;
        lgPhone.price=1000;

        System.out.println("equals via Object ref: " + lg.equals(htc)); // still MyPhone.equals
    }

    // ─────────────────────────────────────────────────────────────
    // Three contract states — no equals/hashCode, hashCode only, both
    // ─────────────────────────────────────────────────────────────

    /** No equals, no hashCode — identity comparison. */
    static class Phone {
        public int price;

        public String toString(){
            return "price="+price;
        }
    }

    /** hashCode() only — collides on price, but equality is identity. */
    static class Smartphone {

        public int price;

        public String toString(){
            return "Smartphone="+price;
        }

        public int hashCode(){
            return price;
        }
    }

    /** Both equals() and hashCode() based on price — full contract. */
    static class MyPhone {
        public int price;

        public String toString(){
            return "MyPhone="+price;
        }

        public int hashCode(){
            return price;
        }

        public boolean equals(Object other){
            if(other==null){
                return false;
            }
            if(other==this){
                return true;
            }
            if(other.getClass() == MyPhone.class){
                //Object has no field price
                //other.price++;
                MyPhone otherMyPhone = (MyPhone) other;
                boolean isEqual = (otherMyPhone.price == this.price);
                return isEqual;
            }
            return false;
        }
    }
}
