package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo.CachedHash;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo.ConstantHash;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo.EqualsOnly;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo.Point;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo.SpreadHash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HashCodeDemoTest — verifies every concept shown in {@link HashCodeDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how hashCode behaves.
 */
@DisplayName("HashCodeDemo — API contracts")
class HashCodeDemoTest {

    // ── 1. The contract ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The contract")
    class ContractTest {

        @Test
        @DisplayName("equal objects have equal hash codes")
        void equalObjectsEqualHashes() {
            assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
            assertThat(new Point(1, 2).hashCode()).isEqualTo(new Point(1, 2).hashCode());
        }

        @Test
        @DisplayName("consistent: repeated calls return the same value")
        void consistent() {
            Point p = new Point(3, 4);
            assertThat(p.hashCode()).isEqualTo(p.hashCode());
        }

        @Test
        @DisplayName("the field order matters: (1,2) and (2,1) differ")
        void orderMatters() {
            assertThat(new Point(1, 2).hashCode()).isNotEqualTo(new Point(2, 1).hashCode());
        }

        @Test
        @DisplayName("unequal objects MAY share a hash code (collisions are legal)")
        void collisionsAreLegal() {
            assertThat("Aa".hashCode()).isEqualTo("BB".hashCode());
            assertThat("Aa").isNotEqualTo("BB");
        }
    }

    // ── 2. equals without hashCode ────────────────────────────────────────────

    @Nested
    @DisplayName("2. equals without hashCode")
    class MissingHashCodeTest {

        @Test
        @DisplayName("a HashSet holds two 'equal' elements")
        void duplicatesInSet() {
            assertThat(HashCodeDemo.sizeWithoutHashCode()).isEqualTo(2);
        }

        @Test
        @DisplayName("contains() fails for an equal object")
        void containsFails() {
            assertThat(HashCodeDemo.containsWithoutHashCode()).isFalse();
        }

        @Test
        @DisplayName("an ArrayList still works because it only uses equals")
        void listStillWorks() {
            List<EqualsOnly> list = new ArrayList<>();
            list.add(new EqualsOnly(1));
            assertThat(list.contains(new EqualsOnly(1))).isTrue();
        }

        @Test
        @DisplayName("the objects ARE equal — only the hash code is missing")
        void objectsAreEqual() {
            assertThat(new EqualsOnly(1)).isEqualTo(new EqualsOnly(1));
        }
    }

    // ── 3. Collisions: legal but slow ─────────────────────────────────────────

    @Nested
    @DisplayName("3. Collisions: legal but slow")
    class CollisionsTest {

        @Test
        @DisplayName("a constant hash code gives a single distinct value")
        void constantHash() {
            List<ConstantHash> items = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                items.add(new ConstantHash(i));
            }
            assertThat(HashCodeDemo.distinctHashes(items)).isEqualTo(1);
        }

        @Test
        @DisplayName("a spread hash code gives one value per object")
        void spreadHash() {
            List<SpreadHash> items = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                items.add(new SpreadHash(i));
            }
            assertThat(HashCodeDemo.distinctHashes(items)).isEqualTo(100);
        }

        @Test
        @DisplayName("even with every key colliding, lookups are still correct")
        void stillCorrect() {
            assertThat(HashCodeDemo.everyConstantHashFound(500)).isTrue();
        }
    }

    // ── 4. How to write one ───────────────────────────────────────────────────

    @Nested
    @DisplayName("4. How to write one")
    class RecipesTest {

        @Test
        @DisplayName("the 31 formula reproduces String.hashCode")
        void manualStringHash() {
            assertThat(HashCodeDemo.manualStringHash("abc")).isEqualTo(96354).isEqualTo("abc".hashCode());
            assertThat(HashCodeDemo.manualStringHash("")).isZero();
        }

        @Test
        @DisplayName("Objects.hash, Arrays.hashCode and List.hashCode use the same recipe")
        void sameRecipe() {
            assertThat(HashCodeDemo.sameRecipeThreeWays()).containsExactly(994, 994, 994);
        }

        @Test
        @DisplayName("edge values: no fields → 1, null → 0")
        void edgeValues() {
            assertThat(Objects.hash()).isEqualTo(1);
            assertThat(Objects.hashCode(null)).isZero();
            assertThat(Arrays.hashCode(new int[0])).isEqualTo(1);
        }

        @Test
        @DisplayName("per-type helpers")
        void perType() {
            assertThat(Boolean.hashCode(true)).isEqualTo(1231);
            assertThat(Boolean.hashCode(false)).isEqualTo(1237);
            assertThat(Long.hashCode(1L << 32)).isEqualTo(1);
            assertThat(Double.hashCode(0.0)).isZero();
        }
    }

    // ── 5. Mutable keys ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Mutable keys")
    class MutableKeyTest {

        @Test
        @DisplayName("after the key mutates, get() and containsKey() miss")
        void lookupMisses() {
            Object[] r = HashCodeDemo.lostKey();
            assertThat(r[0]).isNull();
            assertThat(r[1]).isEqualTo(false);
        }

        @Test
        @DisplayName("…but the entry is still in the map: size 1, value present")
        void entryStillThere() {
            Object[] r = HashCodeDemo.lostKey();
            assertThat(r[2]).isEqualTo(1);
            assertThat(r[3]).isEqualTo(true);
        }

        @Test
        @DisplayName("remove → mutate → re-insert keeps the entry reachable")
        void safeOrder() {
            assertThat(HashCodeDemo.removeMutateReinsert()).isEqualTo("v");
        }
    }

    // ── 6. Stability and caching ──────────────────────────────────────────────

    @Nested
    @DisplayName("6. Stability and caching")
    class StabilityTest {

        @Test
        @DisplayName("an immutable class can compute its hash once")
        void computedOnce() {
            CachedHash cached = new CachedHash("abc");
            int first = cached.hashCode();
            cached.hashCode();
            cached.hashCode();
            assertThat(cached.computations()).isEqualTo(1);
            assertThat(first).isEqualTo("abc".hashCode());
        }

        @Test
        @DisplayName("equal cached objects still have equal hash codes")
        void equalObjects() {
            assertThat(new CachedHash("abc")).isEqualTo(new CachedHash("abc"));
            assertThat(new CachedHash("abc").hashCode()).isEqualTo(new CachedHash("abc").hashCode());
        }

        @Test
        @DisplayName("the identity hash code is stable within one run")
        void identityStable() {
            Object o = new Object();
            assertThat(o.hashCode()).isEqualTo(o.hashCode()).isEqualTo(System.identityHashCode(o));
        }
    }
}
