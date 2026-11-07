package com.vbforge.practice.practice_7_throw_and_throws;

import java.io.IOException;

/**
 * `throws` on a `private` method is still a caller obligation.
 * Making the method private doesn't remove the throws declaration's effect —
 * the calling method must still handle or declare it.
 */
public class PublicVsPrivateThrowsDemo {

    public static void main(String[] args) {
        try {
            ioReadPublic();
            ioReadPrivate();           // still must handle — private doesn't change the contract
            System.out.println("both calls compiled only because of the try/catch (or a throws on main)");
        } catch (IOException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }

    public  static void ioReadPublic()  throws IOException { System.out.println("ioReadPublic ran"); }
    private static void ioReadPrivate() throws IOException { System.out.println("ioReadPrivate ran"); }
}
