package com.vbforge.practice.practice_5_access_through_inheritance_phone;

public class Phone {

    private String model = "Samsung";

    int weight = 10;                  // package-private

    protected String color;

    public int calculatePrice() {
        int price = 0;
        if ("Samsung".equals(model)) {
            price = 1000;
        }
        return price;
    }

    public void printWeight() {
        System.out.println("weight = " + weight);
    }

    public void printColor() {
        System.out.println("color = " + color);
    }

    public void call() {
        System.out.println("Phone.call()");
    }

    protected void sms() {
        System.out.println("Phone.sms()");
    }

    void makePhoto() {                // package-private
        System.out.println("Phone.makePhoto()");
    }

    private void alarm() {
        System.out.println("Phone.alarm()");
    }
}
