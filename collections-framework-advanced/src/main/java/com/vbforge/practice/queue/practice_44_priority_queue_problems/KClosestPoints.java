package com.vbforge.practice.queue.practice_44_priority_queue_problems;

import java.util.Arrays;
import java.util.PriorityQueue;

public class KClosestPoints {
    public static void main(String[] args) {

        int[][] points = {{3,3},{5,-1},{-2,4}};
        int k = 2;
        int[][] result = kClosest(points, k);
        System.out.println("K closest points:");
        for (int[] p : result) {
            System.out.println(Arrays.toString(p));
        }

    }

    public static int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(distance(b), distance(a))
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) maxHeap.poll();
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }

    private static int distance(int[] point) {
        return point[0]*point[0] + point[1]*point[1];
    }

}
