package com.vbforge.practice.practice_1_copy_on_write_shared_list;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Several threads add to one shared CopyOnWriteArrayList<Animal>.
 *
 * CopyOnWriteArrayList is safe for concurrent adds and for iteration while others add.
 * The order of the adds depends on thread scheduling, so the demo prints only
 * order-independent results: the size and the count per concrete type.
 */
public class CopyOnWriteSharedListDemo {

    static class Animal {
        public void makeSound() { System.out.println("Animal sound!"); }
    }
    static class Cat extends Animal {
        @Override public void makeSound() { System.out.println("Meow!"); }
    }
    static class Dog extends Animal {
        @Override public void makeSound() { System.out.println("Bark!"); }
    }

    static class AnimalShelterTask {
        private final List<Animal> animals = new CopyOnWriteArrayList<>();

        void addAnimal(Animal animal) { animals.add(animal); }

        int size() { return animals.size(); }

        Map<String, Integer> countByType() {
            Map<String, Integer> counts = new TreeMap<>();
            for (Animal animal : animals) {
                counts.merge(animal.getClass().getSimpleName(), 1, Integer::sum);
            }
            return counts;
        }
    }

    static class AnimalWorkerAdder implements Runnable {
        private final AnimalShelterTask shelterTask;
        private final Animal animal;

        AnimalWorkerAdder(AnimalShelterTask shelterTask, Animal animal) {
            this.shelterTask = shelterTask;
            this.animal      = animal;
        }

        @Override public void run() { shelterTask.addAnimal(animal); }
    }

    public static void main(String[] args) throws InterruptedException {
        AnimalShelterTask task = new AnimalShelterTask();

        Thread t1 = new Thread(new AnimalWorkerAdder(task, new Dog()));
        Thread t2 = new Thread(new AnimalWorkerAdder(task, new Cat()));
        Thread t3 = new Thread(new AnimalWorkerAdder(task, new Dog()));
        t1.start(); t2.start(); t3.start();
        t1.join();  t2.join();  t3.join();

        System.out.println("size: " + task.size());
        System.out.println("by type: " + task.countByType());
    }
}
