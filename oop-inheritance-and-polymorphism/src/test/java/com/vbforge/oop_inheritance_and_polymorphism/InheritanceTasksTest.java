package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.InheritanceTasks.Director;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceTasks.Employee;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceTasks.Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InheritanceTasksTest — tests for every method in {@link InheritanceTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code InheritanceTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=InheritanceTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("InheritanceTasks — your implementations")
class InheritanceTasksTest {

    private InheritanceTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new InheritanceTasks();
    }

    // ── Task 1: Manager.salary ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Manager.salary()")
    @DisplayName("Task 1 — Manager.salary()")
    class ManagerSalaryTest {

        @Test
        @DisplayName("base 1000 + bonus 250 → 1250")
        void basePlusBonus() {
            assertThat(new Manager("Ann", 1000, 250).salary()).isEqualTo(1250);
        }

        @Test
        @DisplayName("zero bonus → just the base salary")
        void zeroBonus() {
            assertThat(new Manager("Bob", 800, 0).salary()).isEqualTo(800);
        }

        @Test
        @DisplayName("calling twice gives the same answer (no accumulated state)")
        void idempotent() {
            Manager ann = new Manager("Ann", 1000, 250);
            ann.salary();
            assertThat(ann.salary()).isEqualTo(1250);
        }

        @Test
        @DisplayName("the override is used through an Employee reference")
        void throughParentReference() {
            Employee ann = new Manager("Ann", 1000, 250);
            assertThat(ann.salary()).isEqualTo(1250);
        }

        @Test
        @DisplayName("the stored base salary is not modified")
        void baseSalaryUntouched() {
            Manager ann = new Manager("Ann", 1000, 250);
            ann.salary();
            assertThat(ann.getBaseSalary()).isEqualTo(1000);
        }
    }

    // ── Task 2: Manager.describe ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Manager.describe()")
    @DisplayName("Task 2 — Manager.describe()")
    class ManagerDescribeTest {

        @Test
        @DisplayName("\"Ann earns 1250 [manager]\"")
        void basic() {
            assertThat(new Manager("Ann", 1000, 250).describe()).isEqualTo("Ann earns 1250 [manager]");
        }

        @Test
        @DisplayName("zero bonus → \"Bob earns 800 [manager]\"")
        void zeroBonus() {
            assertThat(new Manager("Bob", 800, 0).describe()).isEqualTo("Bob earns 800 [manager]");
        }

        @Test
        @DisplayName("works through an Employee reference")
        void throughParentReference() {
            Employee ann = new Manager("Ann", 1000, 250);
            assertThat(ann.describe()).isEqualTo("Ann earns 1250 [manager]");
        }

        @Test
        @DisplayName("a plain Employee is not tagged")
        void employeeUnchanged() {
            assertThat(new Employee("Zed", 500).describe()).isEqualTo("Zed earns 500");
        }
    }

    // ── Task 3: Director.salary ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Director.salary()")
    @DisplayName("Task 3 — Director.salary()")
    class DirectorSalaryTest {

        @Test
        @DisplayName("1000 + 250 + 900 → 2150")
        void basePlusBonusPlusStock() {
            assertThat(new Director("Eve", 1000, 250, 900).salary()).isEqualTo(2150);
        }

        @Test
        @DisplayName("zero stock grant → same as a manager")
        void zeroStock() {
            assertThat(new Director("Max", 1000, 250, 0).salary()).isEqualTo(1250);
        }

        @Test
        @DisplayName("works through a Manager reference")
        void throughManagerReference() {
            Manager eve = new Director("Eve", 1000, 250, 900);
            assertThat(eve.salary()).isEqualTo(2150);
        }

        @Test
        @DisplayName("works through an Employee reference")
        void throughEmployeeReference() {
            Employee eve = new Director("Eve", 1000, 250, 900);
            assertThat(eve.salary()).isEqualTo(2150);
        }
    }

    // ── Task 4: Director.describe ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Director.describe()")
    @DisplayName("Task 4 — Director.describe()")
    class DirectorDescribeTest {

        @Test
        @DisplayName("\"Eve earns 2150 [manager] [director]\"")
        void basic() {
            assertThat(new Director("Eve", 1000, 250, 900).describe())
                    .isEqualTo("Eve earns 2150 [manager] [director]");
        }

        @Test
        @DisplayName("zero stock grant → \"Max earns 1250 [manager] [director]\"")
        void zeroStock() {
            assertThat(new Director("Max", 1000, 250, 0).describe())
                    .isEqualTo("Max earns 1250 [manager] [director]");
        }

        @Test
        @DisplayName("works through an Employee reference")
        void throughParentReference() {
            Employee eve = new Director("Eve", 1000, 250, 900);
            assertThat(eve.describe()).isEqualTo("Eve earns 2150 [manager] [director]");
        }
    }

    // ── Task 5: inheritanceChain ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement inheritanceChain()")
    @DisplayName("Task 5 — inheritanceChain()")
    class InheritanceChainTest {

        @Test
        @DisplayName("Integer → [Integer, Number, Object]")
        void integer() {
            assertThat(tasks.inheritanceChain(Integer.class))
                    .containsExactly("Integer", "Number", "Object");
        }

        @Test
        @DisplayName("ArrayList → [ArrayList, AbstractList, AbstractCollection, Object]")
        void arrayList() {
            assertThat(tasks.inheritanceChain(ArrayList.class))
                    .containsExactly("ArrayList", "AbstractList", "AbstractCollection", "Object");
        }

        @Test
        @DisplayName("Director → [Director, Manager, Employee, Object]")
        void director() {
            assertThat(tasks.inheritanceChain(Director.class))
                    .containsExactly("Director", "Manager", "Employee", "Object");
        }

        @Test
        @DisplayName("Object → [Object]")
        void objectItself() {
            assertThat(tasks.inheritanceChain(Object.class)).containsExactly("Object");
        }

        @Test
        @DisplayName("an interface has no superclass → only its own name")
        void interfaceType() {
            assertThat(tasks.inheritanceChain(Runnable.class)).containsExactly("Runnable");
        }
    }

    // ── Task 6: isSubtypeOf ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isSubtypeOf()")
    @DisplayName("Task 6 — isSubtypeOf()")
    class IsSubtypeOfTest {

        @ParameterizedTest(name = "{0} is subtype of {1} → {2}")
        @CsvSource({
                "java.lang.Integer,   java.lang.Number,  true",
                "java.lang.Number,    java.lang.Integer, false",
                "java.lang.String,    java.lang.String,  true",
                "java.util.ArrayList, java.util.List,    true",
                "java.util.List,      java.util.ArrayList, false",
                "java.lang.String,    java.lang.Object,  true",
                "java.lang.Object,    java.lang.String,  false",
        })
        @DisplayName("parametrized JDK types")
        void jdkTypes(String child, String parent, boolean expected) throws ClassNotFoundException {
            assertThat(tasks.isSubtypeOf(Class.forName(child), Class.forName(parent))).isEqualTo(expected);
        }

        @Test
        @DisplayName("Director is a Manager and an Employee, but an Employee is not a Director")
        void ownHierarchy() {
            assertThat(tasks.isSubtypeOf(Director.class, Manager.class)).isTrue();
            assertThat(tasks.isSubtypeOf(Director.class, Employee.class)).isTrue();
            assertThat(tasks.isSubtypeOf(Employee.class, Director.class)).isFalse();
        }

        @Test
        @DisplayName("unrelated classes are not subtypes of each other")
        void unrelated() {
            assertThat(tasks.isSubtypeOf(String.class, Integer.class)).isFalse();
        }
    }
}
