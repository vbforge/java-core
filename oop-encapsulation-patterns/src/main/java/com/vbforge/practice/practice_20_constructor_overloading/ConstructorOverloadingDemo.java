package com.vbforge.practice.practice_20_constructor_overloading;

/**
 * Constructor overloading: same name, different parameter lists.
 * The compiler picks the constructor from the argument types at compile time.
 * (Not to be confused with overriding, which is a runtime thing between classes.)
 */
public class ConstructorOverloadingDemo {

    /** 2-arg vs 1-arg. */
    static final class Vehicles {
        public String model;
        public int    price;

        Vehicles(String vehModel, int vehPrice) { model = vehModel; price = vehPrice; }
        Vehicles(String vehModel)               { model = vehModel; }

        void info() { System.out.println("model: " + model + "; price: " + price); }
    }

    /** no-arg vs full-arg; unset fields keep their defaults. */
    static final class Cars {
        public String model;
        public String color;
        public int    price = 0;

        Cars() { System.out.println("Cars() - no-arg"); }

        Cars(String carModel, String carColor, int carPrice) {
            model = carModel;
            color = carColor;
            price = carPrice;
        }

        void printCarInfo() {
            System.out.println("model=" + model + "; color=" + color + "; price=" + price);
        }
    }

    /** Four overloads, including String vs int. */
    static final class Phone {
        public String model;
        public String color;
        public int    price;

        Phone()                                                     { System.out.println("Phone() - no-arg"); }
        Phone(String phoneModel)                                    { model = phoneModel; }
        Phone(int phonePrice)                                       { price = phonePrice; }
        Phone(String phoneModel, String phoneColor, int phonePrice) {
            model = phoneModel; color = phoneColor; price = phonePrice;
        }

        void inform() {
            System.out.println("model=" + model + "; price=" + price + "; color=" + color);
        }
    }

    public static void main(String[] args) {
        System.out.println("-- Vehicles (2-arg vs 1-arg) --");
        Vehicles v1 = new Vehicles("bmw", 50000);
        Vehicles v2 = new Vehicles("bmw");
        v1.info();
        v2.info();

        System.out.println("\n-- Cars (no-arg vs full-arg) --");
        Cars bmw = new Cars();
        bmw.color = "black"; bmw.price = 1; bmw.model = "7";
        bmw.printCarInfo();

        Cars mercedes = new Cars("GLS 200", "green", 3);
        mercedes.printCarInfo();

        System.out.println("\n-- Phone (four overloads) --");
        Phone n1 = new Phone();
        n1.model = "nokia 3200"; n1.price = 125; n1.color = "black";
        n1.inform();

        Phone n2 = new Phone("Nokia 6400");                 // String overload
        n2.price = 210; n2.color = "green";
        n2.inform();

        Phone n3 = new Phone(210);                          // int overload
        n3.model = "Nokia 6400"; n3.color = "green";
        n3.inform();

        Phone n4 = new Phone("Nokia D900", "grey", 800);    // full overload
        n4.inform();
    }
}
