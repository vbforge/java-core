# bigdecimal-and-numeric-precision

> **Phase 1 — Basics** | Java 8+ | Package: `com.vbforge.bigdecimal_and_numeric_precision`

## What's covered

| Topic | Key points |
|---|---|
| Floating-point error | `0.1 + 0.2 != 0.3`; IEEE 754 binary representation; why money must not use `double` |
| `BigDecimal` creation | `new BigDecimal("0.1")` (exact) vs `new BigDecimal(0.1)` (binary noise) vs `BigDecimal.valueOf(0.1)` |
| Scale & rounding | `setScale`, `RoundingMode.HALF_UP`, `HALF_DOWN`, `DOWN`, `CEILING`, `FLOOR` |
| `compareTo` vs `equals` | `2.0` vs `2.00` — same value, different scale; `equals` is scale-sensitive |
| Integer overflow | `Integer.MAX_VALUE + 1` wraps silently; `Math.addExact` throws; `long` widening |
| Money in cents | `long` cents for exact integer arithmetic; convert to `BigDecimal` only for display |

## Files

```
src/
├── main/java/com/vbforge/bigdecimal_and_numeric_precision/
│   ├── BigDecimalDemo.java        ← theory + annotated examples
│   └── BigDecimalTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/bigdecimal_and_numeric_precision/
    ├── BigDecimalDemoTest.java     ← tests for the demo (already green)
    └── BigDecimalTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `BigDecimalTasks.java` and implement all 6 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `calculatePriceWithTax(price, taxRate)` | Money to the cent — tax + total, HALF_UP rounding |
| 2 | `sumPrices(prices)` | Exact `BigDecimal` summation with scale 2 |
| 3 | `splitEqually(amount, parts)` | Money splitting with remainder-cent distribution |
| 4 | `wouldOverflowOnAdd(a, b)` | Overflow detection without performing the addition |
| 5 | `centsToDollars(cents)` | Long cents → `BigDecimal` with scale 2 |
| 6 | `isNumericallyEqual(a, b)` | `compareTo` vs `equals` — value vs value-and-scale |

```bash
mvn test -Dtest=BigDecimalTasksTest
```

All 6 task groups must go **GREEN** ✅

## Key things to remember

```java
// Never use double for money
double bad  = 0.1 + 0.2;                             // 0.30000000000000004 ❌
BigDecimal ok = new BigDecimal("0.1")
        .add(new BigDecimal("0.2"));                 // 0.3 ✅

// Use the String constructor for exact decimals
BigDecimal good = new BigDecimal("0.1");             // ✅ exactly 0.1
BigDecimal bad  = new BigDecimal(0.1);               // ❌ 0.10000000000000000555…
BigDecimal also = BigDecimal.valueOf(0.1);           // ✅ same as "0.1"

// Always set a RoundingMode when reducing scale
new BigDecimal("2.345").setScale(2);                       // ❌ ArithmeticException
new BigDecimal("2.345").setScale(2, RoundingMode.HALF_UP); // ✅ 2.35

// equals() compares scale; compareTo() compares value
new BigDecimal("2.0").equals(new BigDecimal("2.00"));      // ❌ false — different scale
new BigDecimal("2.0").compareTo(new BigDecimal("2.00")) == 0;  // ✅ true

// Overflow protection
int wrapped = Integer.MAX_VALUE + 1;                 // ❌ silently wraps
Math.addExact(Integer.MAX_VALUE, 1);                 // ✅ throws ArithmeticException
long widened = (long) Integer.MAX_VALUE + 1;         // ✅ always safe

// Money in cents — exact and simple
long totalCents = 1999L + 160L;                      // 2159
BigDecimal dollars = BigDecimal.valueOf(totalCents, 2);  // 21.59
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=BigDecimalDemoTest

# Task tests only
mvn test -Dtest=BigDecimalTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.bigdecimal_and_numeric_precision.BigDecimalDemo"
```