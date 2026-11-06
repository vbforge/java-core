package com.vbforge.practice.practice_16_this_keyword;

public class ThisKeywordDemo {

    public static void main(String[] args) {
        Human john = new Human();
        john.setName("John");
        john.setAge(18);
        System.out.println(john);

        Human jack = new Human();
        jack.setName("Jack");
        jack.setAge(19);
        System.out.println(jack);
    }
}
