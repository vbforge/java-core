package com.vbforge.practice.practice_1_executor_future_ordering;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Predictable ordering of concurrent task results.
 *
 * Demonstrates:
 *   - ExecutorService with fixed thread pool
 *   - Callable tasks (return values + can throw)
 *   - Future.get() called IN SUBMISSION ORDER → final results come back in order,
 *     even though individual tasks finish at different times
 *   - Polymorphic return types via Person → Employee / Client
 *
 * Why .get() ordering matters:
 *   Futures may complete in any order. Calling .get() sequentially on futures
 *   in the same order you submitted them guarantees the assembled result list
 *   is in submission order — one of the simplest ways to tame concurrency.
 *
 * Consolidated from: multithreading_type_safety_collections.facilitate_order_results
 *   - Main (with polymorphic Person/Employee/Client)
 *   - PredictableOrderMultithreading (simpler string-only version)
 */
public class OrderedResultsDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        polymorphicTasks();
        simpleTasks();
    }

    // ------------------------------------------------------------
    // Scene 1: polymorphic Person tasks
    // ------------------------------------------------------------
    static void polymorphicTasks() throws ExecutionException, InterruptedException {
        System.out.println("-- Scene 1: polymorphic Person tasks, ordered results --");

        int tasks = 5;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < tasks; i++) {
            final int taskID = i;
            Callable<String> task = () -> {
                Person person = (taskID % 2 == 0)
                        ? new Employee("Employee#" + taskID, taskID + 100)
                        : new Client("Client#" + taskID,     taskID + 500);
                Thread.sleep(100L * taskID);   // later tasks finish slower
                return "Task " + taskID + " : " + person.getInfo();
            };
            futures.add(executor.submit(task));
        }

        // Calling get() in SUBMISSION order → results appear in submission order
        List<String> resultsList = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            resultsList.add(futures.get(i).get());
        }

        resultsList.forEach(System.out::println);
        executor.shutdown();
        System.out.println();
    }

    // ------------------------------------------------------------
    // Scene 2: simpler string tasks, same ordering guarantee
    // ------------------------------------------------------------
    static void simpleTasks() throws ExecutionException, InterruptedException {
        System.out.println("-- Scene 2: simple ordered tasks --");

        int tasks = 5;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < tasks; i++) {
            final int taskID = i;
            Callable<String> task = () -> {
                Thread.sleep(100L * taskID);
                return "Result: " + taskID;
            };
            futures.add(executor.submit(task));
        }

        List<String> results = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            results.add(futures.get(i).get());
        }

        results.forEach(System.out::println);
        executor.shutdown();
        System.out.println();
    }

    // ------------------------------------------------------------
    // Helper types — polymorphic Person hierarchy
    // ------------------------------------------------------------
    enum RoleType {
        EMPLOYEE, CLIENT
    }

    static abstract class Person {
        protected String name;
        protected int    tel;

        Person(String name, int tel) {
            this.name = name;
            this.tel  = tel;
        }

        public abstract String getInfo();
    }

    static class Employee extends Person {
        private final RoleType role = RoleType.EMPLOYEE;

        Employee(String name, int tel) {
            super(name, tel);
        }

        @Override
        public String getInfo() {
            return String.format("[%s] %s (%s)", role.name(), super.name, super.tel);
        }
    }

    static class Client extends Person {
        private final RoleType role = RoleType.CLIENT;

        Client(String name, int tel) {
            super(name, tel);
        }

        @Override
        public String getInfo() {
            return String.format("[%s] %s (%s)", role.name(), super.name, super.tel);
        }
    }
}