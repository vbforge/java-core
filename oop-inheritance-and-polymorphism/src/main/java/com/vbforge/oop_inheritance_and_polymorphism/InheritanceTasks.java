package com.vbforge.oop_inheritance_and_polymorphism;

import java.util.List;

/**
 * InheritanceTasks — implement every method below to make {@code InheritanceTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code InheritanceDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=InheritanceTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The hierarchy {@link Employee} ← {@link Manager} ← {@link Director} is already wired up
 * (constructors, fields, getters). Your job is to write the OVERRIDDEN methods and two
 * reflection helpers. Do not change method signatures or add external libraries — only the
 * Java standard library is needed.
 *
 * <p><b>Order matters for tasks 1–4:</b> {@code describe()} prints the result of {@code salary()},
 * and {@code Director} builds on {@code Manager}. Implement them in the order 1 → 2 → 3 → 4.
 * Tasks 5 and 6 are independent.
 */
public class InheritanceTasks {

    // ── Given: base class (do NOT modify) ──────────────────────────────────────

    /** A regular employee. {@code describe()} deliberately calls the overridable {@code salary()}. */
    public static class Employee {
        private final String name;
        private final int baseSalary;

        public Employee(String name, int baseSalary) {
            this.name = name;
            this.baseSalary = baseSalary;
        }

        public String getName() {
            return name;
        }

        public int getBaseSalary() {
            return baseSalary;
        }

        public int salary() {
            return baseSalary;
        }

        public String describe() {
            return name + " earns " + salary();
        }
    }

    // ── Task 1 & 2: Manager ────────────────────────────────────────────────────

    /** A manager is an employee with a bonus. Constructor and getter are given. */
    public static class Manager extends Employee {
        private final int bonus;

        public Manager(String name, int baseSalary, int bonus) {
            super(name, baseSalary);
            this.bonus = bonus;
        }

        public int getBonus() {
            return bonus;
        }

        /**
         * Overrides {@link Employee#salary()}: a manager earns the employee salary
         * <em>plus</em> the bonus.
         *
         * <p>Examples:
         * <pre>
         *   new Manager("Ann", 1000, 250).salary()  → 1250
         *   new Manager("Bob",  800,   0).salary()  → 800
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The parent's base salary field is {@code private} — ask the parent
         *       for it through its own API instead of re-implementing it.</li>
         *   <li>Calling the method twice must give the same answer (do not store state).</li>
         * </ul>
         *
         * @return base salary plus bonus
         */
        @Override
        public int salary() {
            throw new UnsupportedOperationException("TODO: implement Manager.salary");
        }

        /**
         * Overrides {@link Employee#describe()}: reuse the parent's description and append
         * the tag {@code " [manager]"}.
         *
         * <p>Examples:
         * <pre>
         *   new Manager("Ann", 1000, 250).describe()  → "Ann earns 1250 [manager]"
         *   new Manager("Bob",  800,   0).describe()  → "Bob earns 800 [manager]"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Do not rebuild the "earns" text yourself — extend what the parent already returns.</li>
         *   <li>Look at the numbers in the examples: which {@code salary()} ends up being used?</li>
         * </ul>
         *
         * @return the parent's description followed by {@code " [manager]"}
         */
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: implement Manager.describe");
        }
    }

    // ── Task 3 & 4: Director ───────────────────────────────────────────────────

    /** A director is a manager with a stock grant. Constructor and getter are given. */
    public static class Director extends Manager {
        private final int stockGrant;

        public Director(String name, int baseSalary, int bonus, int stockGrant) {
            super(name, baseSalary, bonus);
            this.stockGrant = stockGrant;
        }

        public int getStockGrant() {
            return stockGrant;
        }

        /**
         * Overrides {@link Manager#salary()}: a director earns the manager salary
         * <em>plus</em> the stock grant.
         *
         * <p>Examples:
         * <pre>
         *   new Director("Eve", 1000, 250, 900).salary()  → 2150
         *   new Director("Max", 1000, 250,   0).salary()  → 1250   (same as a manager)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Do not add the bonus yourself — the class directly above you already knows how.</li>
         * </ul>
         *
         * @return manager salary plus stock grant
         */
        @Override
        public int salary() {
            throw new UnsupportedOperationException("TODO: implement Director.salary");
        }

        /**
         * Overrides {@link Manager#describe()}: reuse the manager description and append
         * the tag {@code " [director]"}.
         *
         * <p>Examples:
         * <pre>
         *   new Director("Eve", 1000, 250, 900).describe()  → "Eve earns 2150 [manager] [director]"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>You are two levels below {@link Employee}; you only get to call one level up.</li>
         * </ul>
         *
         * @return the manager's description followed by {@code " [director]"}
         */
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: implement Director.describe");
        }
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the simple names of {@code type} and all its superclasses, starting with
     * {@code type} itself and ending with {@code "Object"}.
     *
     * <p>For an interface (which has no superclass) the result contains only the interface's
     * own simple name.
     *
     * <p>Examples:
     * <pre>
     *   inheritanceChain(Integer.class)    → ["Integer", "Number", "Object"]
     *   inheritanceChain(ArrayList.class)  → ["ArrayList", "AbstractList", "AbstractCollection", "Object"]
     *   inheritanceChain(Director.class)   → ["Director", "Manager", "Employee", "Object"]
     *   inheritanceChain(Object.class)     → ["Object"]
     *   inheritanceChain(Runnable.class)   → ["Runnable"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getSuperclass()} returns {@code null} once you reach the top.</li>
     *   <li>{@link Class#getSimpleName()} gives the name without the package.</li>
     * </ul>
     *
     * @param type the class to start from (never {@code null})
     * @return simple names from {@code type} up to {@code Object}
     */
    public List<String> inheritanceChain(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement inheritanceChain");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code child} IS-A {@code parent}: the same class, a subclass
     * at any depth, or a class that implements the interface {@code parent}.
     *
     * <p>Examples:
     * <pre>
     *   isSubtypeOf(Integer.class,   Number.class)   → true
     *   isSubtypeOf(Number.class,    Integer.class)  → false   (direction matters)
     *   isSubtypeOf(String.class,    String.class)   → true    (a type is a subtype of itself)
     *   isSubtypeOf(ArrayList.class, List.class)     → true    (interfaces count)
     *   isSubtypeOf(String.class,    Object.class)   → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class} already has a method that answers "can a value of this type be assigned
     *       to that type?" — be careful about which class is the receiver and which is the argument.</li>
     * </ul>
     *
     * @param child  the candidate subtype
     * @param parent the candidate supertype
     * @return {@code true} if a {@code child} can be used wherever a {@code parent} is expected
     */
    public boolean isSubtypeOf(Class<?> child, Class<?> parent) {
        throw new UnsupportedOperationException("TODO: implement isSubtypeOf");
    }
}
