package com.vbforge.basics.controlflow;

/**
 * Demonstrates Java control flow: if-else, switch (classic + expression +
 * pattern matching), for/while/do-while loops, break, continue, and labels.
 *
 * <p>Java versions covered:
 * <ul>
 *   <li>Java 8  — classic if-else, classic switch, all loop types</li>
 *   <li>Java 14 — switch expressions (arrow syntax, yield)</li>
 *   <li>Java 17 — pattern matching instanceof</li>
 *   <li>Java 21 — switch pattern matching with guards</li>
 * </ul>
 */
public class ControlFlowDemo {

    // ── 1. if / else if / else ─────────────────────────────────────────────────
    /**
     * The fundamental branching construct.
     *
     * <p>Rules:
     * <ul>
     *   <li>Condition must be strictly {@code boolean} — no implicit int-to-bool conversion</li>
     *   <li>Braces are optional for single statements, but always recommended</li>
     *   <li>Chains are evaluated top-down — first matching branch wins</li>
     * </ul>
     */
    public String classifyTemperature(int celsius) {
        if (celsius < 0) {
            return "freezing";
        } else if (celsius < 10) {
            return "cold";
        } else if (celsius < 20) {
            return "cool";
        } else if (celsius < 30) {
            return "warm";
        } else {
            return "hot";
        }
    }

    /**
     * Nested if — valid but watch out for the "dangling else" trap.
     * An {@code else} always binds to the nearest preceding {@code if}.
     */
    public String loginCheck(boolean hasAccount, boolean isVerified) {
        if (hasAccount) {
            if (isVerified) {
                return "welcome";
            } else {
                return "verify your email";
            }
        } else {
            return "please register";
        }
    }


    // ── 2. Classic switch statement ────────────────────────────────────────────
    /**
     * The traditional switch — works on: byte, short, int, char, String, enums.
     *
     * <p>Key behaviours:
     * <ul>
     *   <li><b>Fall-through</b>: without {@code break}, execution continues into the next case</li>
     *   <li><b>default</b>: runs when no case matches — position does not matter, but bottom is convention</li>
     *   <li>Case values must be compile-time constants</li>
     * </ul>
     */
    public String getDayType(String day) {
        String type;
        switch (day.toUpperCase()) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
                type = "weekday";
                break;
            case "SATURDAY":
            case "SUNDAY":
                type = "weekend";
                break;
            default:
                type = "unknown";
        }
        return type;
    }

    /**
     * Intentional fall-through — sometimes useful, always document it clearly.
     * Here we accumulate permissions level by level.
     */
    public int getPermissionLevel(String role) {
        int level = 0;
        switch (role) {
            case "ADMIN":
                level += 100;   // falls through
                // fall-through intentional — ADMIN also gets MOD and USER perms
            case "MODERATOR":
                level += 10;    // falls through
                // fall-through intentional
            case "USER":
                level += 1;
                break;
            default:
                level = 0;
        }
        return level;
    }


    // ── 3. Switch expression (Java 14+) ───────────────────────────────────────
    /**
     * Switch expressions return a value directly. Two syntaxes:
     *
     * <p><b>Arrow syntax</b> ({@code ->}): no fall-through, no break needed.
     * <p><b>Traditional with yield</b>: used when a case needs multiple statements.
     *
     * <p>Exhaustiveness: for enums and sealed types, the compiler checks all cases are covered.
     */
    public String getSeasonArrow(int month) {
        return switch (month) {
            case 12, 1, 2  -> "winter";
            case 3, 4, 5   -> "spring";
            case 6, 7, 8   -> "summer";
            case 9, 10, 11 -> "autumn";
            default        -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }

    /**
     * Switch expression with {@code yield} — needed when a case block has multiple statements.
     */
    public int getWorkingDays(int month, int year) {
        int days = switch (month) {
            case 2 -> {
                boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                yield isLeap ? 29 : 28;         // yield returns the value from this block
            }
            case 4, 6, 9, 11 -> 30;
            default          -> 31;
        };
        return days;
    }


    // ── 4. Pattern matching — instanceof (Java 16+) ───────────────────────────
    /**
     * Old style required an explicit cast after instanceof check.
     * Pattern matching combines check + binding in one expression.
     */
    public String describeObject(Object obj) {
        // Old style (pre Java 16):
        // if (obj instanceof String) { String s = (String) obj; ... }

        // Pattern matching — binding variable 's' is scoped to the if block
        if (obj instanceof String s) {
            return "String of length " + s.length();
        } else if (obj instanceof Integer i) {
            return "Integer: " + (i > 0 ? "positive" : i < 0 ? "negative" : "zero");
        } else if (obj instanceof int[] arr) {
            return "int array of length " + arr.length;
        } else if (obj == null) {
            return "null";
        } else {
            return "unknown type: " + obj.getClass().getSimpleName();
        }
    }


    // ── 5. Switch pattern matching (Java 21+) ─────────────────────────────────
    /**
     * Combines switch expressions with type patterns and guards ({@code when}).
     * The compiler enforces exhaustiveness for sealed hierarchies.
     */
    public String formatValue(Object obj) {
        return switch (obj) {
            case Integer i when i < 0     -> "negative int: "  + i;
            case Integer i                -> "positive int: "  + i;
            case Double d when d.isNaN()  -> "NaN double";
            case Double d                 -> "double: "        + d;
            case String s when s.isEmpty()-> "empty string";
            case String s                 -> "string: \""      + s + "\"";
            case null                     -> "null";
            default                       -> "other: "         + obj.getClass().getSimpleName();
        };
    }


    // ── 6. for loop ───────────────────────────────────────────────────────────
    /**
     * Classic for — best when iteration count is known upfront.
     *
     * <p>Anatomy: {@code for (init; condition; update)}
     * <ul>
     *   <li>init    — runs once before the loop</li>
     *   <li>condition — checked before each iteration; loop stops when false</li>
     *   <li>update  — runs after each iteration body</li>
     * </ul>
     */
    public int sumArray(int[] numbers) {
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        return sum;
    }

    /** Iterating backwards — a common pattern. */
    public int[] reverseArray(int[] numbers) {
        int[] result = new int[numbers.length];
        for (int i = numbers.length - 1; i >= 0; i--) {
            result[numbers.length - 1 - i] = numbers[i];
        }
        return result;
    }


    // ── 7. Enhanced for loop (for-each) ───────────────────────────────────────
    /**
     * Cleaner syntax for iterating over arrays and Iterables.
     * Trade-off: no index access, no modification of the array during iteration.
     */
    public int sumList(java.util.List<Integer> numbers) {
        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }
        return sum;
    }


    // ── 8. while loop ─────────────────────────────────────────────────────────
    /**
     * Best when the number of iterations is not known in advance.
     * Condition checked BEFORE entering the loop body — may execute 0 times.
     */
    public int countDigits(int number) {
        if (number == 0) return 1;
        int count = 0;
        int n = Math.abs(number);
        while (n > 0) {
            n /= 10;
            count++;
        }
        return count;
    }


    // ── 9. do-while loop ──────────────────────────────────────────────────────
    /**
     * Condition checked AFTER the body — guaranteed to run at least once.
     * Classic use: input validation loops, menu-driven programs.
     */
    public int collatzSteps(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive");
        if (n == 1) return 0;   // already at 1 — zero steps needed
        int steps = 0;
        do {
            n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
            steps++;
        } while (n != 1);
        return steps;
    }


    // ── 10. break and continue ────────────────────────────────────────────────
    /**
     * {@code break}    — exits the innermost loop or switch immediately.
     * {@code continue} — skips the rest of the current iteration, moves to next.
     */
    public int findFirst(int[] numbers, int target) {
        int foundIndex = -1;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] == target) {
                foundIndex = i;
                break;              // no need to keep searching
            }
        }
        return foundIndex;
    }

    public int sumEvens(int[] numbers) {
        int sum = 0;
        for (int n : numbers) {
            if (n % 2 != 0) {
                continue;           // skip odd numbers
            }
            sum += n;
        }
        return sum;
    }


    // ── 11. Labeled break and continue ───────────────────────────────────────
    /**
     * Labels let you break/continue an OUTER loop from inside a nested one.
     * Use sparingly — a method extraction is often cleaner.
     */
    public int[] findInMatrix(int[][] matrix, int target) {
        outer:                                      // label on the outer loop
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == target) {
                    return new int[]{row, col};     // found — return directly
                }
            }
        }
        return new int[]{-1, -1};                  // not found
    }

    /**
     * Labeled continue — skip the rest of an outer loop iteration.
     * Here we skip any row that contains a negative number.
     */
    public int sumPositiveRows(int[][] matrix) {
        int total = 0;
        outer:
        for (int[] row : matrix) {
            for (int val : row) {
                if (val < 0) continue outer;        // skip entire row
            }
            for (int val : row) {
                total += val;
            }
        }
        return total;
    }


    // ── Main — quick smoke run ─────────────────────────────────────────────────
    public static void main(String[] args) {
        ControlFlowDemo demo = new ControlFlowDemo();

        System.out.println("\n=== 1. if-else ===");
        System.out.println(demo.classifyTemperature(-5));   // freezing
        System.out.println(demo.classifyTemperature(25));   // warm

        System.out.println("\n=== 2. Classic switch ===");
        System.out.println(demo.getDayType("Monday"));      // weekday
        System.out.println(demo.getDayType("Sunday"));      // weekend
        System.out.println("ADMIN level: " + demo.getPermissionLevel("ADMIN"));       // 111
        System.out.println("USER level: "  + demo.getPermissionLevel("USER"));        // 1

        System.out.println("\n=== 3. Switch expression ===");
        System.out.println(demo.getSeasonArrow(7));         // summer
        System.out.println("Feb 2024 days: " + demo.getWorkingDays(2, 2024)); // 29

        System.out.println("\n=== 4. Pattern matching instanceof ===");
        System.out.println(demo.describeObject("hello"));   // String of length 5
        System.out.println(demo.describeObject(-42));       // Integer: negative

        System.out.println("\n=== 5. Switch pattern matching ===");
        System.out.println(demo.formatValue(-3));           // negative int: -3
        System.out.println(demo.formatValue(""));           // empty string

        System.out.println("\n=== 6-7. Loops ===");
        System.out.println(demo.sumArray(new int[]{1, 2, 3, 4, 5}));   // 15
        System.out.println(demo.countDigits(12345));                    // 5

        System.out.println("\n=== 8. break / continue ===");
        System.out.println(demo.findFirst(new int[]{3, 7, 2, 7, 9}, 7)); // 1
        System.out.println(demo.sumEvens(new int[]{1, 2, 3, 4, 5, 6})); // 12
    }
}
