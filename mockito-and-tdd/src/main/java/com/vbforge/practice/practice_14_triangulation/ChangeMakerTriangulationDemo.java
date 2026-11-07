package com.vbforge.practice.practice_14_triangulation;

import com.vbforge.practice.practice_11_tdd_check_helper.MiniCheck;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Triangulation: a hard-coded answer (fake it) passes the first example; the second example
 * forces a general rule; the third forces a different one. Making change with coins.
 */
public class ChangeMakerTriangulationDemo {

    private static final int[] AMOUNTS = {1, 2, 5, 7, 16};
    private static final List<List<Integer>> EXPECTED = Arrays.asList(
            Arrays.asList(1),
            Arrays.asList(1, 1),
            Arrays.asList(5),
            Arrays.asList(5, 1, 1),
            Arrays.asList(10, 5, 1));

    static final Function<Integer, List<Integer>> SKELETON = amount -> Collections.<Integer>emptyList();
    static final Function<Integer, List<Integer>> FAKE_IT = amount -> Arrays.asList(1);
    static final Function<Integer, List<Integer>> ONES = amount -> greedy(amount, new int[] {1});
    static final Function<Integer, List<Integer>> FIVES_AND_ONES = amount -> greedy(amount, new int[] {5, 1});
    static final Function<Integer, List<Integer>> ALL_COINS = amount -> greedy(amount, new int[] {10, 5, 1});

    static List<Integer> greedy(int amount, int[] coinsLargestFirst) {
        List<Integer> coins = new ArrayList<Integer>();
        int rest = amount;
        for (int coin : coinsLargestFirst) {
            while (rest >= coin) {
                coins.add(coin);
                rest -= coin;
            }
        }
        return coins;
    }

    public static void main(String[] args) {
        System.out.println("TEST 1: change(1) = [1]");
        run("skeleton (empty list)", SKELETON, 1);
        run("fake it: always [1]", FAKE_IT, 1);

        System.out.println("TEST 2: change(2) = [1, 1]  (triangulate: the constant no longer works)");
        run("fake it", FAKE_IT, 2);
        run("only 1-coins", ONES, 2);

        System.out.println("TEST 3: change(5) = [5]");
        run("only 1-coins", ONES, 3);
        run("coins 5 and 1", FIVES_AND_ONES, 3);

        System.out.println("TEST 4: change(7) = [5, 1, 1]  (already green: confirms the rule)");
        run("coins 5 and 1", FIVES_AND_ONES, 4);

        System.out.println("TEST 5: change(16) = [10, 5, 1]");
        run("coins 5 and 1", FIVES_AND_ONES, 5);
        run("coins 10, 5 and 1", ALL_COINS, 5);
    }

    private static void run(String label, Function<Integer, List<Integer>> impl, int tests) {
        System.out.println("  " + label);
        MiniCheck check = new MiniCheck();
        for (int i = 0; i < tests; i++) {
            final int amount = AMOUNTS[i];
            check.check("change(" + amount + ") == " + EXPECTED.get(i), EXPECTED.get(i), () -> impl.apply(amount));
        }
        System.out.println("    -> " + check.summary() + (check.isGreen() ? "  GREEN" : "  RED"));
    }
}
