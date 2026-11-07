package com.vbforge.practice.queue.practice_40_queue_problems;

import java.util.ArrayDeque;
import java.util.Deque;

public class RottingOranges {
    public static void main(String[] args) {

        int[][] grid = {
                {2,1,1},
                {1,1,0},
                {0,1,1}
        };
        System.out.println("Minutes to rot all: " + orangesRotting(grid)); // 4

    }

    public static int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        // Initialize queue with all rotten oranges
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.offerLast(new int[]{r, c});
                if (grid[r][c] == 1) fresh++;
            }
        }

        int minutes = 0;
        int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};

        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            minutes++;
            for (int i = 0; i < size; i++) {
                int[] orange = queue.pollFirst();
                for (int[] d : directions) {
                    int x = orange[0] + d[0], y = orange[1] + d[1];
                    if (x >= 0 && y >= 0 && x < rows && y < cols && grid[x][y] == 1) {
                        grid[x][y] = 2;
                        fresh--;
                        queue.offerLast(new int[]{x, y});
                    }
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

}
