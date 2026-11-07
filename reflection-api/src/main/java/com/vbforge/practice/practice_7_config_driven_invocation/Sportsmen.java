package com.vbforge.practice.practice_7_config_driven_invocation;

/** Target class named in the configuration; jump() is the configured action. */
public class Sportsmen {
    private String name;
    private int age;

    public Sportsmen(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Sportsmen() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void jump() {
        System.out.println("Sportsmen.jump();");
    }

    @Override
    public String toString() {
        return "Sportsmen{name='" + name + "', age=" + age + '}';
    }
}
