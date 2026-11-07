package com.vbforge.practice.queue.practice_42_priority_queue_comparators;

import java.util.PriorityQueue;

public class PriorityQueueCustomCarsComparator {
    public static void main(String[] args) {

        PriorityQueue<Car> cars = new PriorityQueue<>((o1, o2) -> {
            return Double.compare(o2.horsePower(), o1.horsePower()); //desc order
        });

        cars.offer(new Car(1, "BMW", 100.0));
        cars.offer(new Car(2, "Mercedes", 120.0));
        cars.offer(new Car(3, "Audi", 150.0));
        cars.offer(new Car(4, "Ford", 130.0));
        cars.offer(new Car(5, "Volvo", 100.0));

        System.out.println("Cars by horsePower: ");
        while (!cars.isEmpty()) {
            System.out.println(cars.poll());
        }

    }

    public record Car(int id, String model, double horsePower){

        public Car {
            if(horsePower < 0) throw new IllegalArgumentException("horsePower must be >= 0");
            if(model == null) throw new NullPointerException("model must not be null");
            if(id <= 0) throw new IllegalArgumentException("id must be > 0");
        }

        @Override
        public String toString() {
            return String.format("%d. %s, HP: %.2f", id, model, horsePower);
        }
    }

}
