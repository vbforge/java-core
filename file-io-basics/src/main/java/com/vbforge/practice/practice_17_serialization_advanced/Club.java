package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.Serializable;

public class Club implements Serializable {

    private transient int id;
    private String name;
    private int squadAmount;

    public Club(int id, String name, int squadAmount) {
        this.id = id;
        this.name = name;
        this.squadAmount = squadAmount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSquadAmount() {
        return squadAmount;
    }

    @Override
    public String toString() {
        return id +
                " : " + name +
                "; squad = " + squadAmount;
    }
}