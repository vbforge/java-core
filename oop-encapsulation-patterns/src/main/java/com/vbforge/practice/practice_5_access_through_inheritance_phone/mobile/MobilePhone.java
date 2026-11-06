package com.vbforge.practice.practice_5_access_through_inheritance_phone.mobile;

import com.vbforge.practice.practice_5_access_through_inheritance_phone.Phone;

/** Subclass in ANOTHER package: sees public + protected members of Phone, nothing else. */
public class MobilePhone extends Phone {

    public void createColor(String colorValue) {
        color = colorValue;                       // protected field, reachable through inheritance
    }

    /** NOT an override: Phone.alarm() is private, so this is a brand-new method. */
    public void alarm() {
        System.out.println("MobilePhone.alarm()");
    }

    @Override
    public void call() {
        System.out.println("MobilePhone.call()");
    }

    public void parentCall() {
        super.call();
        sms();
    }

    /** Override that WIDENS protected -> public (allowed; narrowing is not). */
    @Override
    public void sms() {
        System.out.println("MobilePhone.sms()");
    }

    /**
     * NOT an override: Phone.makePhoto() is package-private and invisible from this package,
     * so this is a new method that only happens to share the name.
     * (@Override here would be a compile error.)
     */
    public void makePhoto() {
        System.out.println("MobilePhone.makePhoto()");
    }
}
