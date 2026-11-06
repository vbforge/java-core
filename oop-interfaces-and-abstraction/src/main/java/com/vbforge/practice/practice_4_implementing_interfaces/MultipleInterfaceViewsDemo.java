package com.vbforge.practice.practice_4_implementing_interfaces;

/**
 * A class may implement several interfaces. One Dolphin object can be held as Dolphin,
 * Mammal, Animal or Swimmer; each reference type exposes only its own methods.
 */
public class MultipleInterfaceViewsDemo {

    interface Animal {
        void eat();
        void sleep();
    }

    interface Mammal extends Animal {
        void walk();
    }

    interface Swimmer {
        void swim();
    }

    static class Dolphin implements Mammal, Swimmer {
        @Override public void eat()   { System.out.println("Dolphin eats");   }
        @Override public void sleep() { System.out.println("Dolphin sleeps"); }
        @Override public void walk()  { System.out.println("Dolphin walks");  }
        @Override public void swim()  { System.out.println("Dolphin swims");  }
    }

    public static void main(String[] args) {
        Dolphin dolphin = new Dolphin();

        Animal asAnimal = dolphin;
        asAnimal.eat();
        asAnimal.sleep();
        // asAnimal.swim();   // compile error: not part of Animal

        Mammal asMammal = dolphin;
        asMammal.walk();

        Swimmer asSwimmer = dolphin;
        asSwimmer.swim();
    }
}
