package com.vbforge.practice.practice_3_enum_constant_specific_bodies;

/** Each constant overrides toString() in its own body (no abstract method needed). */
public class ConstantSpecificToStringDemo {

    public static void main(String[] args) {
        System.out.println(SeasonType.SPRING);
        System.out.println(SeasonType.SUMMER);
        System.out.println(SeasonType.WINTER);
        System.out.println(SeasonType.AUTUMN);
        /* Spring - cold-warm season
           Summer - hot season
           Winter - cold season
           Autumn - cool season */
    }
}

enum SeasonType {
    WINTER {
        @Override
        public String toString() {
            return "Winter - cold season";
        }
    },
    SPRING {
        @Override
        public String toString() {
            return "Spring - cold-warm season";
        }
    },
    SUMMER {
        @Override
        public String toString() {
            return "Summer - hot season";
        }
    },
    AUTUMN {
        @Override
        public String toString() {
            return "Autumn - cool season";
        }
    }
}
