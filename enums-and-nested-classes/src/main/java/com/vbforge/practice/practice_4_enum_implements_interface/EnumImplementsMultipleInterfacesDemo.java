package com.vbforge.practice.practice_4_enum_implements_interface;

/**
 * One enum implementing five small interfaces: each constant can be used as a Run, Bike, Swim, Gym or Rest.
 * Each method prints the training action for the month.
 */
public class EnumImplementsMultipleInterfacesDemo {

    public static void main(String[] args) {
        TrainingMonth april = TrainingMonth.APRIL;
        System.out.println(april + " (month " + april.getMonth() + ") training plan:");
        Run run = april;
        Bike bike = april;
        Swim swim = april;
        run.running();
        bike.rideBike();
        swim.swimPool();

        TrainingMonth december = TrainingMonth.DECEMBER;
        System.out.println(december + " (month " + december.getMonth() + ") training plan:");
        Swim winterSwim = december;
        Rest rest = december;
        winterSwim.swimPool();
        rest.reload();

        TrainingMonth january = TrainingMonth.JANUARY;
        System.out.println(january + " (month " + january.getMonth() + ") training plan:");
        Gym gym = january;
        gym.gymExercise();
    }
}

interface Bike {
    void rideBike();
}

interface Gym {
    void gymExercise();
}

interface Rest {
    void reload();
}

interface Run {
    void running();
}

interface Swim {
    void swimming();
    void swimPool();
}

enum TrainingMonth implements Gym, Swim, Bike, Run, Rest {

    JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6), JULY(7), AUGUST(8),
    SEPTEMBER(9), OCTOBER(10), NOVEMBER(11), DECEMBER(12);

    private final int month;

    TrainingMonth(int m) {
        this.month = m;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public void rideBike() {
        System.out.println("biking");
    }

    @Override
    public void gymExercise() {
        System.out.println("gym exercise");
    }

    @Override
    public void reload() {
        System.out.println("resting period");
    }

    @Override
    public void running() {
        System.out.println("running");
    }

    @Override
    public void swimming() {
        System.out.println("swimming");
    }

    @Override
    public void swimPool() {
        System.out.println("swimming in pool");
    }
}
