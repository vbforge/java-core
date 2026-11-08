package com.vbforge.practice.practice_10_timeouts_and_conditional_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

/**
 * Technique: controlling WHEN and HOW LONG tests run.
 * Some of these tests are skipped or aborted on purpose: that is what the annotation demonstrates.
 * In a build report they appear as "skipped", not as failures.
 */
class TestControlTest {

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    // ----- timeouts -----

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)      // fails the test if it runs longer (checked after it finishes)
    void levenshtein_ofTwoThousandChars_finishesInTime() {
        int distance = EditDistance.levenshtein(repeat('a', 2_000), repeat('b', 2_000));

        assertThat(distance).isEqualTo(2_000);
    }

    @Test
    void assertTimeout_runsToCompletion_thenChecksTheClock() {
        int distance = assertTimeout(Duration.ofSeconds(2),
                () -> EditDistance.levenshtein(repeat('a', 1_000), repeat('a', 1_000)));

        assertThat(distance).isZero();
    }

    @Test
    void assertTimeoutPreemptively_abortsRunawayCode() {
        // runs in ANOTHER thread and is interrupted when the limit is hit: use it for code that might hang
        // (caution: thread-bound things such as ThreadLocal or transactions do not carry over)
        int result = assertTimeoutPreemptively(Duration.ofSeconds(2), () -> EditDistance.levenshtein("abc", "abd"));

        assertThat(result).isEqualTo(1);
    }

    // ----- repetition -----

    @RepeatedTest(value = 5, name = "run {currentRepetition} of {totalRepetitions}")
    void repeatedTest_pureFunction_givesSameAnswerEveryTime(RepetitionInfo info) {
        // repeating a deterministic test proves nothing new; it is for hunting FLAKY tests (threads, time, ordering)
        assertThat(EditDistance.levenshtein("kitten", "sitting")).isEqualTo(3);
        assertThat(info.getCurrentRepetition()).isBetween(1, info.getTotalRepetitions());
    }

    // ----- conditional execution -----

    @Test
    @EnabledForJreRange(min = JRE.JAVA_17)
    void enabledOnlyOnModernJava() {
        assertThat(Runtime.version().feature()).isGreaterThanOrEqualTo(17);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void notOnWindows_pathSeparatorIsSlash() {
        assertThat(java.io.File.separator).isEqualTo("/");
    }

    @Test
    @EnabledIfSystemProperty(named = "practice.slow", matches = "true")
    void onlyWhenSwitchedOn_runWithDashDpracticeSlashSlowEqualsTrue() {
        // never runs by default: opt-in with  mvn test -Dpractice.slow=true  (surefire forwards -D properties)
        assertThat(EditDistance.levenshtein(repeat('a', 5_000), repeat('b', 5_000))).isEqualTo(5_000);
    }

    @Test
    void assumption_falseMeansAbortedNotFailed() {
        assumeTrue(System.getenv("CI_RUN_INTEGRATION") != null, "integration environment not available");

        // only reached when the assumption holds; otherwise the test is reported as ABORTED (skipped)
        assertThat(true).isTrue();
    }

    @Test
    @Disabled("example of a temporarily disabled test: always give the reason, and an owner or ticket in real life")
    void disabledWithReason() {
        throw new AssertionError("never executed");
    }

    // ----- tags: select groups from the command line -----

    @Test
    @Tag("fast")
    void taggedFast() {
        assertThat(EditDistance.levenshtein("a", "a")).isZero();
    }

    @Test
    @Tag("slow")
    void taggedSlow() {
        // run only fast tests:  mvn test -Dgroups=fast      exclude slow ones:  mvn test -DexcludedGroups=slow
        assertThat(EditDistance.levenshtein(repeat('x', 3_000), repeat('y', 3_000))).isEqualTo(3_000);
    }
}
