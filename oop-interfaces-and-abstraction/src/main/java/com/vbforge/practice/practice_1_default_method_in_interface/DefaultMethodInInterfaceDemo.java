package com.vbforge.practice.practice_1_default_method_in_interface;

/** A default method in an interface: implementers inherit checkResult() and can call it, or be called through the interface type. */
public class DefaultMethodInInterfaceDemo {

    public static void main(String[] args) {
        Athlete athlete = new Athlete(true);
        System.out.println(athlete.jump());
        System.out.println("didResultSuccess: " + athlete.didResultSuccess());
        athlete.printInfo();

        System.out.println();
        Jumping asInterface = new Athlete(false);
        System.out.println(asInterface.jump());
        System.out.println("checkResult(true) via the default method: " + asInterface.checkResult(true));
        Athlete cast = (Athlete) asInterface;   // the cast is needed to reach Athlete-only methods
        System.out.println("didResultSuccess: " + cast.didResultSuccess());
        cast.printInfo();
    }
}

interface Jumping {
    String jump();

    default boolean checkResult(boolean successful) {
        return successful;
    }
}

class Athlete implements Jumping {

    private final boolean success;

    public Athlete(boolean success) {
        this.success = success;
    }

    @Override
    public String jump() {
        return "Athlete jumps!";
    }

    public boolean didResultSuccess() {
        return checkResult(this.success);
    }

    public void printInfo() {
        System.out.println(this.jump() + ": " + checkResult(this.success));
    }
}
