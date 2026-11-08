package com.vbforge.practice.practice_4_object_graphs;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** Serialization preserves sharing: two fields pointing to ONE object still point to ONE (new) object afterwards. */
public class SharedReferenceDemo {

    static class Address implements Serializable {
        private static final long serialVersionUID = 1L;
        String city;

        Address(String city) {
            this.city = city;
        }
    }

    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;
        Address home;
        Address work;
    }

    public static void main(String[] args) throws Exception {
        Address shared = new Address("Kyiv");
        Employee e = new Employee();
        e.home = shared;
        e.work = shared;

        Employee copy = SerializationHelper.roundTrip(e);

        System.out.println("original: home == work: " + (e.home == e.work));
        System.out.println("copy:     home == work: " + (copy.home == copy.work));
        System.out.println("copy address is a new object: " + (copy.home != shared));
        copy.home.city = "Lviv";
        System.out.println("change through copy.home is seen by copy.work: " + copy.work.city);
        System.out.println("original untouched: " + shared.city);
    }
}
