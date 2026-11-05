package com.vbforge.practice.practice_14_annotation_driven_security_proxy;

/**
 * The @BankingAnnotation is placed on the IMPLEMENTATION methods (see AccountManagerImpl):
 * the proxy handler looks the annotation up on the target class, not on this interface.
 */
public interface AccountManager {

    double depositInCash(int accountId, int amount);

    boolean withdraw(int accountId, int amount);

    boolean convert(double amount);

    boolean transfer(int accountId, double amount);

}
