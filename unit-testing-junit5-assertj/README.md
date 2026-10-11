# unit-testing-junit5-assertj

> **Phase 1 — Basics** | Java 8+ | Package: `com.vbforge.unit_testing_junit5_assertj`

## What's covered

| Topic | Key points |
|---|---|
| JUnit 5 lifecycle | `@BeforeAll` / `@BeforeEach` / `@AfterEach` / `@AfterAll`; fresh state per test |
| Test structure | `@DisplayName`, `@Nested`, AAA pattern (Arrange-Act-Assert) |
| Parameterized tests | `@ParameterizedTest` + `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource` |
| AssertJ core | `assertThat`, chaining, `isEqualTo`, `contains`, `extracting`, `isBetween` |
| Exception testing | `assertThatThrownBy`, `assertThatExceptionOfType`, `assertThatCode` |
| Test independence | Why `@BeforeEach` beats shared `static` state; flaky-test causes |
| TDD kata | RED → GREEN → REFACTOR; FizzBuzz written test-first; why step 5 forces ordering |

## Files

```
src/
├── main/java/com/vbforge/unit_testing_junit5_assertj/
│   ├── LifecycleDemo.java                    ← lifecycle & structure reference
│   ├── LifecycleTasks.java                   ← 🎯 string utility tasks
│   ├── ParameterizedDemo.java                ← @ParameterizedTest reference
│   ├── ParameterizedTasks.java               ← 🎯 math utility tasks
│   ├── AssertionsAndExceptionsDemo.java      ← AssertJ & exceptions reference
│   ├── AssertionsAndExceptionsTasks.java     ← 🎯 exception-rich tasks
│   ├── TddKataDemo.java                      ← FizzBuzz as a finished TDD journal
│   └── TddKataTasks.java                     ← 🎯 your TDD exercise
└── test/java/com/vbforge/unit_testing_junit5_assertj/
├── LifecycleDemoTest.java                 ← green — read this first
├── LifecycleTasksTest.java                ← 🧪 @Disabled, activate as you go
├── ParameterizedDemoTest.java             ← green — every argument source
├── ParameterizedTasksTest.java            ← 🧪 @Disabled
├── AssertionsAndExceptionsDemoTest.java   ← green — AssertJ in depth
├── AssertionsAndExceptionsTasksTest.java  ← 🧪 @Disabled
├── TddKataDemoTest.java                   ← green — six-step TDD journal
└── TddKataTasksTest.java                  ← 🧪 only Step 1 active
```

## 🎯 Tasks

Implement the 12 methods across three `Tasks` classes. Then do the TDD kata.
Each has a detailed Javadoc.

| # | Pair | Method | Topic |
|---|---|---|---|
| 1 | Lifecycle | `isPalindrome(s)` | String scan + case-insensitive compare |
| 2 | Lifecycle | `reverse(s)` | `StringBuilder.reverse` + null guard |
| 3 | Lifecycle | `countVowels(s)` | Character classification |
| 4 | Lifecycle | `capitalizeWords(s)` | Word splitting + `Character.toUpperCase` |
| 5 | Parameterized | `isPrime(n)` | Loops to `sqrt(n)`; edge cases below 2 |
| 6 | Parameterized | `gcd(a, b)` | Euclidean algorithm; `Math.abs` + loop |
| 7 | Parameterized | `factorial(n)` | Guard `n < 0` and `n > 20` for overflow |
| 8 | Parameterized | `maxOf(arr)` | Null/empty guards + linear scan |
| 9 | Assertions | `divide(a, b)` | Throw `ArithmeticException` on `b == 0` |
| 10 | Assertions | `parseAge(s)` | Trim, parse, range check, rethrow |
| 11 | Assertions | `firstElement(list)` | Throw `NoSuchElementException` on empty/null |
| 12 | Assertions | `clamp(v, min, max)` | Guard `min > max`; use `Math.max`/`Math.min` |
| 13 | TDD kata | `fizzbuzz(n)` | Write your own tests first — RED → GREEN → REFACTOR |

```bash
mvn test -Dtest=LifecycleTasksTest
mvn test -Dtest=ParameterizedTasksTest
mvn test -Dtest=AssertionsAndExceptionsTasksTest
mvn test -Dtest=TddKataTasksTest
```

All 13 task groups must go **GREEN** ✅

## 🧪 TDD kata — how this one differs

Every other `*TasksTest` in this repo ships with the full suite pre-written
and `@Disabled` at the `@Nested` level. You remove `@Disabled`, implement the
method, run the tests, done.

**`TddKataTasksTest` is the opposite.** It ships with only the *first* test
active. Your job is to write the remaining tests yourself — one at a time,
each one RED before you touch production code.

### The RED → GREEN → REFACTOR cycle

The kata is **FizzBuzz**, run as a proper TDD session. Here is the exact log:

| Step | Test added | Result | Production change in `TddKataTasks` |
|---|---|---|---|
| 1 | `fizzbuzz(1) == "1"` | **RED** | `return String.valueOf(n);` |
| 2 | `fizzbuzz(2) == "2"` | GREEN | *(no change — the general case was already covered)* |
| 3 | `fizzbuzz(3) == "Fizz"` | **RED** | add `if (n % 3 == 0) return "Fizz";` |
| 4 | `fizzbuzz(5) == "Buzz"` | **RED** | add `if (n % 5 == 0) return "Buzz";` |
| 5 | `fizzbuzz(15) == "FizzBuzz"` | **RED** | add `if (n % 15 == 0) return "FizzBuzz";` **as the first check** |
| 6 | (refactor) | still GREEN | reorder/clean up if you like |

### The one rule

> **Never write production code that a failing test did not ask for.**

If you find yourself writing `if (n % 3 == 0)` before you have added and run
`fizzbuzz(3)` — you are not doing TDD. Undo, run the test first, watch it fail,
then write the code.

### Step 5 is the entire lesson

Step 5 is the reason this kata is worth doing. When you add
`assert fizzbuzz(15) == "FizzBuzz"`:

- If your checks are `% 3`, then `% 5`, then `% 15` — the test fails, because
  `15 % 3 == 0` fires first and returns `"Fizz"`.
- If your checks are `% 15`, then `% 3`, then `% 5` — the test passes.

Writing the test first is what forces you to *see* that ordering matters.
Reading the finished implementation, you might never have noticed.

### Reference solution

`TddKataDemo.java` + `TddKataDemoTest.java` show the finished kata — a full
journal of the six steps, annotated with the RED→GREEN log. **Read them
*after* you finish your own run** — reading them first defeats the exercise.

## Key things to remember

```java
// Lifecycle — @BeforeEach gives every test a fresh start
@BeforeEach
void setUp() { tasks = new Tasks(); }         // ✅ independent tests
// static Tasks shared = null;                // ❌ state leaks, order-dependent failures

// AAA — Arrange / Act / Assert, with visible separation
@Test
void adds() {
    int a = 2, b = 3;                          // Arrange
    int result = calc.add(a, b);               // Act
    assertThat(result).isEqualTo(5);           // Assert
}

// @ParameterizedTest — one method, many inputs, one result per input
@ParameterizedTest
@CsvSource({"1,2,3", "0,0,0", "-1,1,0"})       // 3 separate IDE results
void adds(int a, int b, int expected) {
    assertThat(calc.add(a, b)).isEqualTo(expected);
}

// AssertJ — prefer fluent chains over many assert calls
assertThat(names)
        .hasSize(3)
        .contains("Bob")
        .doesNotContain("Dave");               // ✅ one chained assertion

// Exception testing — assertThatThrownBy / assertThatExceptionOfType
assertThatThrownBy(() -> calc.divide(5, 0))    // ✅ AssertJ style
        .isInstanceOf(ArithmeticException.class)
        .hasMessageContaining("zero");

assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> parser.parse(null)); // ✅ typed variant

// "No exception" — assertThatCode
assertThatCode(() -> calc.divide(10, 2))
        .doesNotThrowAnyException();

// @Disabled at the @Nested level, not on the test class
@Nested
@Disabled("Remove when you implement X")        // ✅ whole group skipped
class SomeTest { … }

// @BeforeAll / @AfterAll must be static (JUnit 5, default lifecycle)
@BeforeAll
static void beforeAll() { … }

// TDD — the cycle, in code
// 1. RED: write the test first
assertThat(tasks.fizzbuzz(3)).isEqualTo("Fizz");   // ← fails, method throws
// 2. GREEN: simplest code that makes it pass
public String fizzbuzz(int n) { return n % 3 == 0 ? "Fizz" : String.valueOf(n); }
// 3. REFACTOR: clean up, all tests stay green
// 4. Repeat — one behaviour at a time

// The rule
// ❌ if (n % 3 == 0) …               // written BEFORE the test → not TDD
// ✅ test → RED → code → GREEN → next test
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only (all green from day one)
mvn test -Dtest='*DemoTest'

# Task tests only (activate as you implement)
mvn test -Dtest='*TasksTest'

# One pair at a time
mvn test -Dtest=LifecycleTasksTest
mvn test -Dtest=ParameterizedTasksTest
mvn test -Dtest=AssertionsAndExceptionsTasksTest
mvn test -Dtest=TddKataTasksTest

# Run any demo's main()
mvn exec:java -Dexec.mainClass="com.vbforge.unit_testing_junit5_assertj.LifecycleDemo"
```

---