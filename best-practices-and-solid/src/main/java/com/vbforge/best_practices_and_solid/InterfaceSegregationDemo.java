package com.vbforge.best_practices_and_solid;

import java.util.Arrays;
import java.util.List;

/**
 * <b>I — Interface Segregation Principle (ISP).</b>
 *
 * <p>"Clients should not be forced to depend on methods they do not use." Prefer
 * several small, role-focused interfaces over one fat interface.
 *
 * <p>Sections:
 * <ol>
 *   <li>A fat interface that forces implementers to fake methods</li>
 *   <li>Segregated role interfaces</li>
 *   <li>Clients that ask for the smallest interface they need</li>
 * </ol>
 */
public class InterfaceSegregationDemo {

    // ── 1. The fat interface ──────────────────────────────────────────────────
    /**
     * What it is: {@link FatWorker} bundles work, eating and sleeping. A robot works
     * but does neither of the others, so {@link FatRobot} implements them by throwing.
     *
     * <p>Why it hurts:
     * <ul>
     *   <li>Implementers carry dead methods (and the temptation to throw — see LSP)</li>
     *   <li>Callers that only need {@code work()} still recompile when {@code sleep()} changes</li>
     *   <li>{@link #lunchBreakBad} compiles for robots and explodes at runtime</li>
     * </ul>
     */
    public interface FatWorker {
        int work();

        String eat();

        String sleep();
    }

    public static class FatHuman implements FatWorker {
        @Override
        public int work() {
            return 8;
        }

        @Override
        public String eat() {
            return "human eats lunch";
        }

        @Override
        public String sleep() {
            return "human sleeps";
        }
    }

    public static class FatRobot implements FatWorker {
        @Override
        public int work() {
            return 24;
        }

        @Override
        public String eat() {
            throw new UnsupportedOperationException("Robots do not eat");
        }

        @Override
        public String sleep() {
            throw new UnsupportedOperationException("Robots do not sleep");
        }
    }

    public static String lunchBreakBad(List<FatWorker> workers) {
        StringBuilder meals = new StringBuilder();
        for (FatWorker worker : workers) {
            meals.append(worker.eat()).append(';');
        }
        return meals.toString();
    }


    // ── 2. Segregated role interfaces ─────────────────────────────────────────
    /**
     * What it is: one tiny interface per <i>role</i>. A class implements exactly the
     * roles it can really play.
     *
     * <p>Why it matters: {@code Robot} no longer has fake methods, and a method that
     * takes {@code List<? extends Eatable>} <b>cannot</b> be given a robot — the
     * compiler enforces what the fat version left to luck.
     *
     * <p>Gotcha: do not over-split. Methods that are always used together
     * (say {@code open()} and {@code close()}) belong in the same interface.
     */
    @FunctionalInterface
    public interface Workable {
        int work();
    }

    @FunctionalInterface
    public interface Eatable {
        String eat();
    }

    @FunctionalInterface
    public interface Sleepable {
        String sleep();
    }

    public static class Human implements Workable, Eatable, Sleepable {
        private final String name;
        private final int workUnits;
        private final String meal;

        public Human(String name, int workUnits, String meal) {
            this.name = name;
            this.workUnits = workUnits;
            this.meal = meal;
        }

        @Override
        public int work() {
            return workUnits;
        }

        @Override
        public String eat() {
            return name + " eats " + meal;
        }

        @Override
        public String sleep() {
            return name + " sleeps";
        }
    }

    public static class Robot implements Workable {
        private final int workUnits;

        public Robot(int workUnits) {
            this.workUnits = workUnits;
        }

        @Override
        public int work() {
            return workUnits;
        }
    }


    // ── 3. Clients depend on the smallest interface they need ─────────────────
    /**
     * What it is: {@link #printReport} needs only {@link Printer}. A cheap
     * {@link BasicPrinter} and a fancy {@link AllInOneDevice} are both accepted.
     *
     * <p>Why it matters: with one fat {@code MultiFunctionDevice} interface, the cheap
     * printer would have to fake scanning and faxing. The JDK follows the same idea:
     * {@code Runnable}, {@code Comparable} and {@code AutoCloseable} are one-method
     * interfaces that classes combine as needed.
     */
    public interface Printer {
        String print(String document);
    }

    public interface DocumentScanner {
        String scan(String document);
    }

    public interface Fax {
        String fax(String document, String number);
    }

    public static class BasicPrinter implements Printer {
        @Override
        public String print(String document) {
            return "printed: " + document;
        }
    }

    public static class AllInOneDevice implements Printer, DocumentScanner, Fax {
        @Override
        public String print(String document) {
            return "printed: " + document;
        }

        @Override
        public String scan(String document) {
            return "scanned: " + document;
        }

        @Override
        public String fax(String document, String number) {
            return "faxed " + document + " to " + number;
        }
    }

    public static String printReport(Printer printer, String report) {
        return printer.print("REPORT: " + report);
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void fatInterfaceDemo() {
        try {
            lunchBreakBad(Arrays.<FatWorker>asList(new FatHuman(), new FatRobot()));
        } catch (UnsupportedOperationException e) {
            System.out.println("lunchBreakBad crashed at runtime: " + e.getMessage());
        }
    }

    static void roleInterfacesDemo() {
        List<Eatable> diners = Arrays.<Eatable>asList(new Human("Ann", 8, "soup"), () -> "snack");
        for (Eatable diner : diners) {
            System.out.println(diner.eat());
        }
        // Arrays.<Eatable>asList(new Robot(5)) would not even compile — that is the point.
    }

    static void smallestInterfaceDemo() {
        System.out.println(printReport(new BasicPrinter(), "Q1"));
        System.out.println(printReport(new AllInOneDevice(), "Q2"));
    }

    public static void main(String[] args) {
        fatInterfaceDemo();
        System.out.println("---");
        roleInterfacesDemo();
        System.out.println("---");
        smallestInterfaceDemo();
    }
}
