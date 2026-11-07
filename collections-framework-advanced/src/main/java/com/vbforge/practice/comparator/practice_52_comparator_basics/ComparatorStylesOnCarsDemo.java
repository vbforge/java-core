package com.vbforge.practice.comparator.practice_52_comparator_basics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorStylesOnCarsDemo {
    public static void main(String[] args) {

        //initializing the collection of cars
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(2010, "Dodge"));
        cars.add(new Car(2001, "Audi"));
        cars.add(new Car(2005, "BMW"));
        cars.add(new Car(2020, "Mercedes"));
        cars.add(new Car(2024, "Nissan"));

        System.out.println("Sorting cars by year ASC (from oldest to youngest):");
        //Comparator is a functional interface so we can use lambda expression for method compare
        System.out.println("\n1. Lambda: sorting cars by year asc (from oldest to youngest):");
        cars.sort(((o1, o2) -> {
            if (o1.year() > o2.year()) return 1;
            if (o1.year() < o2.year()) return -1;
            else return 0;
        }));
        System.out.println(cars);

        System.out.println("\n2. Lambda + Integer.compare: ");
        cars.sort(((o1, o2) -> {
            return Integer.compare(o1.year(), o2.year());
        }));
        System.out.println(cars);

        System.out.println("\n3. Method reference Comparator.comparingInt(Car::year): ");
        cars.sort((Comparator.comparingInt(Car::year)));
        System.out.println(cars);
    }

    //model class
    record Car(int year, String brand){
        @Override
        public String toString() {
            return String.format("%s (%d)", brand, year);
        }
    }
}
