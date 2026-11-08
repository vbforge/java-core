package com.vbforge.practice.practice_13_branch_and_condition_coverage;

import static com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision.APPROVED;
import static com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision.APPROVED_WITH_REVIEW;
import static com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision.REJECTED_NO_INCOME;
import static com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision.REJECTED_RISK;
import static com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision.REJECTED_UNDERAGE;
import static org.assertj.core.api.Assertions.assertThat;

import com.vbforge.practice.practice_13_branch_and_condition_coverage.LoanApprover.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Technique: choosing tests by COVERAGE CRITERIA, from weakest to strongest.
 *
 *  statement coverage : every line runs at least once          -> 1 test is enough here (the APPROVED path does not even touch D1 true)
 *  branch coverage    : every decision is true AND false once   -> D1..D4 each need both outcomes
 *  condition coverage : every atomic condition true AND false   -> A, B, C, D, E, F
 *  MC/DC              : every condition is shown to change the outcome ON ITS OWN, others fixed (needs n+1 rows per decision)
 *  boundary           : the >= / < edges: 17|18, 49_999|50_000, 699|700, 39|40, 599|600, 19|20
 *
 * 100% line coverage says "the code ran", not "the behaviour was checked": a test without assertions also gives 100%.
 * Mutation testing (e.g. PIT) is the next level: it changes `>=` into `>` and fails the build if no test notices.
 * The boundary tests below are exactly the ones that kill such mutants.
 * Args: age, employed, annualIncome, creditScore, debtRatioPercent -> decision
 */
@DisplayName("LoanApprover coverage")
class LoanApproverCoverageTest {

    @Test
    @DisplayName("statement coverage is weak: one happy-path test executes the approving line but proves little")
    void statementCoverage_onlyHappyPath() {
        assertThat(LoanApprover.decide(30, true, 40_000, 720, 25)).isEqualTo(APPROVED);
    }

    @Nested
    @DisplayName("branch coverage: each decision both ways")
    class BranchCoverage {

        @ParameterizedTest(name = "[{index}] age={0} employed={1} income={2} score={3} debt={4}% -> {5}")
        @CsvSource({
                "17, true,  40000, 720, 25, REJECTED_UNDERAGE",       // D1 true
                "30, false, 40000, 720, 25, REJECTED_NO_INCOME",      // D1 false, D2 true
                "30, true,  40000, 720, 25, APPROVED",                // D2 false, D3 true
                "30, true,  40000, 650, 10, APPROVED_WITH_REVIEW",    // D3 false, D4 true
                "30, true,  40000, 650, 30, REJECTED_RISK"            // D4 false
        })
        void everyDecisionTakenBothWays(int age, boolean employed, int income, int score, int debt, Decision expected) {
            assertThat(LoanApprover.decide(age, employed, income, score, debt)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("MC/DC: each condition independently flips the outcome")
    class Mcdc {

        // D2 = employed(A) || income >= 50000(B); score/debt are good so a pass of D2 ends in APPROVED
        @ParameterizedTest(name = "D2 row: employed={0} income={1} -> {2}")
        @CsvSource({
                "true,  10000, APPROVED",            // A=T B=F : passes because of A
                "false, 10000, REJECTED_NO_INCOME",  // A=F B=F : flipping A alone (vs row 1) changes the outcome
                "false, 60000, APPROVED"             // A=F B=T : flipping B alone (vs row 2) changes the outcome
        })
        void decision2_employedOrHighIncome(boolean employed, int income, Decision expected) {
            assertThat(LoanApprover.decide(30, employed, income, 750, 10)).isEqualTo(expected);
        }

        // D3 = score >= 700 (C) && debt < 40 (D); employed so D2 is passed
        @ParameterizedTest(name = "D3 row: score={0} debt={1}% -> {2}")
        @CsvSource({
                "750, 10, APPROVED",                // C=T D=T
                "650, 10, APPROVED_WITH_REVIEW",    // C=F D=T : flipping C alone changes the outcome (falls through to D4)
                "750, 45, REJECTED_RISK"            // C=T D=F : flipping D alone changes the outcome
        })
        void decision3_goodScoreAndLowDebt(int score, int debt, Decision expected) {
            assertThat(LoanApprover.decide(30, true, 0, score, debt)).isEqualTo(expected);
        }

        // D4 = score >= 600 (E) && debt < 20 (F), reached only when D3 is false (score < 700 or debt >= 40)
        @ParameterizedTest(name = "D4 row: score={0} debt={1}% -> {2}")
        @CsvSource({
                "650, 10, APPROVED_WITH_REVIEW",    // E=T F=T
                "599, 10, REJECTED_RISK",           // E=F F=T : flipping E alone
                "650, 25, REJECTED_RISK"            // E=T F=F : flipping F alone
        })
        void decision4_fairScoreAndVeryLowDebt(int score, int debt, Decision expected) {
            assertThat(LoanApprover.decide(30, true, 0, score, debt)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("boundaries: the tests that kill >= / > mutants")
    class Boundaries {

        @ParameterizedTest(name = "age {0} -> {1}")
        @CsvSource({"17, REJECTED_UNDERAGE", "18, APPROVED"})
        void ageEdge(int age, Decision expected) {
            assertThat(LoanApprover.decide(age, true, 0, 750, 10)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "unemployed, income {0} -> {1}")
        @CsvSource({"49999, REJECTED_NO_INCOME", "50000, APPROVED"})
        void incomeEdge(int income, Decision expected) {
            assertThat(LoanApprover.decide(30, false, income, 750, 10)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "score {0}, debt {1}% -> {2}")
        @CsvSource({
                "699, 10, APPROVED_WITH_REVIEW", "700, 10, APPROVED",
                "700, 39, APPROVED",             "700, 40, REJECTED_RISK",
                "600, 19, APPROVED_WITH_REVIEW", "600, 20, REJECTED_RISK",
                "599, 19, REJECTED_RISK"
        })
        void scoreAndDebtEdges(int score, int debt, Decision expected) {
            assertThat(LoanApprover.decide(30, true, 0, score, debt)).isEqualTo(expected);
        }
    }
}
