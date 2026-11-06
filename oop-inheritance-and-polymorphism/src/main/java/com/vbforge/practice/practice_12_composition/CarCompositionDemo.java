package com.vbforge.practice.practice_12_composition;

/**
 * Composition - the "HAS-A" relationship.
 *
 * Prefer composition over inheritance (Effective Java, Item 18).
 *
 *   Inheritance = IS-A  (Dog IS-A Animal)
 *   Composition = HAS-A (Car HAS-A Engine)
 *
 * Choose composition when:
 *   - the relationship is "has-a", not "is-a"
 *   - you want to reuse behavior without coupling to a hierarchy
 *   - the superclass may change in incompatible ways
 *   - you need behavior from several sources (Java has no multiple class inheritance)
 */
public class CarCompositionDemo {

    // --- Components (the "parts") ---
    static class Engine {
        private final int horsepower;
        private final String type;     // "petrol", "electric", "hybrid"
        private boolean running = false;

        public Engine(int horsepower, String type) {
            this.horsepower = horsepower;
            this.type       = type;
        }

        public void start() {
            running = true;
            System.out.println("  Engine started (" + horsepower + "hp " + type + ")");
        }

        public void stop() {
            running = false;
            System.out.println("  Engine stopped");
        }

        public boolean isRunning() { return running;    }
        public int     getHp()     { return horsepower; }
        public String  getType()   { return type;       }
    }

    static class GpsSystem {
        private String currentLocation = "Unknown";

        public void updateLocation(String location) {
            this.currentLocation = location;
            System.out.println("  GPS: location updated -> " + location);
        }

        public String getCurrentLocation() { return currentLocation; }
    }

    static class AudioSystem {
        private String currentTrack = "Silence";

        public void play(String track) {
            currentTrack = track;
            System.out.println("  Audio: playing \"" + track + "\"");
        }

        public String getCurrentTrack() { return currentTrack; }
    }

    // --- Composite (the "whole") ---
    static class Car {
        private final String model;
        private final Engine      engine;       // HAS-A Engine
        private final GpsSystem   gps;          // HAS-A GPS
        private final AudioSystem audio;        // HAS-A AudioSystem

        public Car(String model, Engine engine) {
            this.model  = model;
            this.engine = engine;
            this.gps    = new GpsSystem();
            this.audio  = new AudioSystem();
        }

        public void start() {
            System.out.println(model + " starting...");
            engine.start();
        }

        public void stop() {
            System.out.println(model + " stopping...");
            engine.stop();
        }

        public void navigateTo(String destination) { gps.updateLocation(destination); }
        public void playMusic(String track)        { audio.play(track); }

        // Expose component state without exposing the components themselves
        public boolean isRunning()          { return engine.isRunning();       }
        public String  getCurrentLocation() { return gps.getCurrentLocation(); }
        public String  getCurrentTrack()    { return audio.getCurrentTrack();  }
        public String  getModel()           { return model;                    }
        public int     getHorsepower()      { return engine.getHp();           }
    }

    public static void main(String[] args) {
        // Car HAS-A Engine, GPS, AudioSystem
        Car tesla = new Car("Tesla Model 3", new Engine(283, "electric"));

        System.out.println("-- Car with composed components --");
        tesla.start();
        tesla.navigateTo("123 Main Street");
        tesla.playMusic("Bohemian Rhapsody");
        System.out.println("Running: " + tesla.isRunning());
        System.out.println("Location: " + tesla.getCurrentLocation());
        tesla.stop();

        // Swap the engine without changing the Car class - composition flexibility
        System.out.println("\n-- Different engine, same Car shell --");
        Car hybrid = new Car("Toyota Prius", new Engine(121, "hybrid"));
        hybrid.start();
        System.out.println(hybrid.getModel() + " has " + hybrid.getHorsepower() + "hp");
        hybrid.stop();
    }
}
