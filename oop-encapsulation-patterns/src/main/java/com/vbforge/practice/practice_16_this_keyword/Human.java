package com.vbforge.practice.practice_16_this_keyword;

/**
 * 'this.name = name' separates the field from the parameter.
 * Without 'this.', 'name = name' would assign the parameter to itself.
 */
public class Human {

    private String name;
    private int    age;

    public void setName(String name) { this.name = name; }
    public String getName()          { return name; }

    public void setAge(int age)      { this.age = age; }
    public int  getAge()             { return age; }

    @Override
    public String toString() {
        return name + " " + age;
    }
}
