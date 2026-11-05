package com.vbforge.practice.practice_14_annotation_driven_security_proxy;

/** Calls every AccountManager method through the security proxy. */
public class SecurityProxyDemo {

    public static void main(String[] args) {
        AccountManager manager = SecurityFactory.registerSecurityObject(new AccountManagerImpl());

        manager.depositInCash(1, 100);   // HIGH
        manager.withdraw(1, 50);         // HIGH
        manager.convert(12.5);           // not annotated -> no security line
        manager.transfer(1, 20.0);       // MEDIUM (default)
    }
}
