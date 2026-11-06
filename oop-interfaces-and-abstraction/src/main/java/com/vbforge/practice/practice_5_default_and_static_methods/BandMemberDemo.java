package com.vbforge.practice.practice_5_default_and_static_methods;

/**
 * Class inheritance + several interfaces + default methods.
 *
 *   - BandMember extends Human AND implements Vocalist + Guitarist.
 *   - Guitarist extends PlaySong (interface inheritance).
 *   - PlaySong has a default playSong(String); Guitarist adds a default that calls it.
 *   - Vocalist declares abstract sing(); BandMember implements it and overrides Human.printName().
 *
 * A class extends ONE class but implements MANY interfaces; the two defaults here have
 * different signatures, so there is no conflict to resolve.
 */
public class BandMemberDemo {

    static class Human {
        protected String name;

        Human(String name) {
            this.name = name;
        }

        public void printName() {
            System.out.println(name);
        }
    }

    interface PlaySong {
        default void playSong(String songName) {
            System.out.println("Playing " + songName);
        }
    }

    interface Guitarist extends PlaySong {
        default void playFavoriteSong() {
            System.out.println("Playing my favorite song");
            playSong("My first song");   // inherited default
        }
    }

    interface Vocalist {
        void sing();
    }

    static class BandMember extends Human implements Vocalist, Guitarist {

        BandMember(String name) {
            super(name);
        }

        @Override
        public void printName() {
            System.out.println("Band Member: " + name);
        }

        @Override
        public void sing() {
            System.out.println("la la la");
        }
    }

    public static void main(String[] args) {
        Human john      = new Human("john");
        Human louis     = new BandMember("louis");
        BandMember jack = new BandMember("jack");

        john.printName();         // Human.printName
        louis.printName();        // BandMember.printName (dynamic dispatch through a Human reference)
        jack.printName();         // BandMember.printName

        jack.sing();              // fulfils Vocalist.sing()
        jack.playFavoriteSong();  // Guitarist default -> PlaySong default
    }
}
