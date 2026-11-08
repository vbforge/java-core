package com.vbforge.practice.practice_13_branch_and_condition_coverage;

public class LoanApproverDemo {
    public static void main(String[] args) {
        System.out.println(LoanApprover.decide(30, true, 40_000, 720, 25));   // APPROVED
        System.out.println(LoanApprover.decide(17, true, 40_000, 720, 25));   // REJECTED_UNDERAGE
        System.out.println(LoanApprover.decide(30, false, 40_000, 720, 25));  // REJECTED_NO_INCOME
    }
}
