package com.vbforge.practice.practice_4_members_inspection_and_public_access;

import java.lang.reflect.Method;

/** getDeclaredMethod(name, paramTypes) + invoke(target, args): without and with arguments. */
public class InvokeMethodDemo {

    public static void main(String[] args) throws Exception {
        // no arguments
        Player player = Player.class.getDeclaredConstructor().newInstance();
        Method doAction = Player.class.getDeclaredMethod("doAction");
        doAction.invoke(player);

        // with an argument: look-up uses the exact parameter type (int, not Integer)
        Phone phone = Phone.class.getDeclaredConstructor().newInstance();
        phone.model = "LG";
        Method info = Phone.class.getDeclaredMethod("info");
        info.invoke(phone);

        Method priceDiscount = Phone.class.getDeclaredMethod("priceDiscount", int.class);
        System.out.println("priceDiscount(2000) = " + priceDiscount.invoke(phone, 2000));
        System.out.println("priceDiscount(4000) = " + priceDiscount.invoke(phone, 4000));
    }
}
