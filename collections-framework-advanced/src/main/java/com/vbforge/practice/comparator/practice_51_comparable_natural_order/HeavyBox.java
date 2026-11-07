package com.vbforge.practice.comparator.practice_51_comparable_natural_order;


import java.util.Objects;

public class HeavyBox extends Box implements Comparable<HeavyBox>{

    private int weight;

    public HeavyBox(double width, double height, double depth, int weight) {
        super(width, height, depth);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "HeavyBox{" +
                "weight=" + weight +
                '}' + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HeavyBox heavyBox = (HeavyBox) o;
        return weight == heavyBox.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weight);
    }

    @Override
    public int compareTo(HeavyBox o) {
        int k = Integer.compare(weight, o.weight);
        if(k != 0)return k;
        return Double.compare(getVolume(), o.getVolume());

    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
