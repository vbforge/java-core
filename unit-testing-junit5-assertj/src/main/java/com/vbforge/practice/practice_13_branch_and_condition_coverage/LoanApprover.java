package com.vbforge.practice.practice_13_branch_and_condition_coverage;

/**
 * Decision logic with compound conditions: the playground for statement, branch and condition coverage.
 *
 *  D1: age < 18
 *  D2: !(employed || income >= 50_000)                (condition A = employed, B = highIncome)
 *  D3: creditScore >= 700 && debtRatioPercent < 40     (condition C = goodScore, D = lowDebt)
 *  D4: creditScore >= 600 && debtRatioPercent < 20     (condition E = fairScore, F = veryLowDebt)
 */
public final class LoanApprover {

    public enum Decision { APPROVED, APPROVED_WITH_REVIEW, REJECTED_UNDERAGE, REJECTED_NO_INCOME, REJECTED_RISK }

    private LoanApprover() {
    }

    public static Decision decide(int age, boolean employed, int annualIncome, int creditScore, int debtRatioPercent) {
        if (age < 18) {                                                       // D1
            return Decision.REJECTED_UNDERAGE;
        }
        if (!(employed || annualIncome >= 50_000)) {                          // D2
            return Decision.REJECTED_NO_INCOME;
        }
        if (creditScore >= 700 && debtRatioPercent < 40) {                    // D3
            return Decision.APPROVED;
        }
        if (creditScore >= 600 && debtRatioPercent < 20) {                    // D4
            return Decision.APPROVED_WITH_REVIEW;
        }
        return Decision.REJECTED_RISK;
    }
}
