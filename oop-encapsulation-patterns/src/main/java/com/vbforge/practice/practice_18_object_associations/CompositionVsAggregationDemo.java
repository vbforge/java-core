package com.vbforge.practice.practice_18_object_associations;

public class CompositionVsAggregationDemo {

    public static void main(String[] args) {

        Furniture[] furniture = new Furniture[] {
                new Furniture("bed", 150),
                new Furniture("cupboard", 250),
                new Furniture("table", 35),
                new Furniture("armchair", 80),
        };
        Apartment flat1 = new Apartment("Bronco", 3050, "brick", 23, furniture);

        System.out.println("Information about the first apartment:\n" + flat1);
        System.out.println("Total furniture cost is: $" + flat1.getTotalFurnitureCost());
    }
}
