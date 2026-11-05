package com.vbforge.practice.practice_11_inner_classes;

/** An inner class as a helper that computes statistics over the outer object's private array. */
public class InnerClassOverOuterArrayDemo {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        OuterClass outerClass = new OuterClass(nums);
        outerClass.analyze();   // max, min, avg
    }
}

class OuterClass {

    private final int[] arr;

    public OuterClass(int[] nums) {
        this.arr = nums;
    }

    public void analyze() {
        InnerClass innerClass = new InnerClass();
        System.out.println(innerClass.getMax());
        System.out.println(innerClass.getMin());
        System.out.println(innerClass.getAvg());
    }

    class InnerClass {

        public int getMin() {
            int min = Integer.MAX_VALUE;
            for (int j : arr) {
                if (j < min) {
                    min = j;
                }
            }
            return min;
        }

        public int getMax() {
            int max = Integer.MIN_VALUE;
            for (int j : arr) {
                if (j > max) {
                    max = j;
                }
            }
            return max;
        }

        private double getAvg() {
            double sum = 0.0;
            for (int j : arr) {
                sum += j;
            }
            return sum / arr.length;
        }
    }
}
