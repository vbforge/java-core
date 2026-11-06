package com.vbforge.practice.practice_5_access_through_inheritance_phone;

import com.vbforge.practice.practice_5_access_through_inheritance_phone.mobile.MobilePhone;

public class PhoneAccessDemo {

    public static void main(String[] args) {

        System.out.println("---------- Phone (same package) ----------");
        Phone p1 = new Phone();

        System.out.println("price=" + p1.calculatePrice());   // private 'model' used inside the class

        p1.weight = 20;                                       // package-private
        p1.printWeight();

        p1.color = "White";                                   // protected, same package
        p1.printColor();

        p1.call();
        p1.sms();
        p1.makePhoto();
        // p1.alarm();                                        // compile error: private

        System.out.println("---------- MobilePhone (subclass, other package) ----------");
        MobilePhone mp1 = new MobilePhone();

        mp1.createColor("Green");
        mp1.printColor();
        mp1.alarm();                                          // MobilePhone's own method

        mp1.call();
        mp1.parentCall();

        mp1.sms();
        mp1.makePhoto();                                      // MobilePhone's own method

        System.out.println("---------- same object through the Phone type ----------");
        Phone viaParent = mp1;
        viaParent.call();         // overridden -> MobilePhone.call()
        viaParent.makePhoto();    // not overridden -> Phone.makePhoto()
    }
}
