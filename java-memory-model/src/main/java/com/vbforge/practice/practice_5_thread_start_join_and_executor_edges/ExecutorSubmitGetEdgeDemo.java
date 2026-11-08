package com.vbforge.practice.practice_5_thread_start_join_and_executor_edges;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The executor versions of the same edges: submitting a task happens-before the task runs,
 * and the task's actions happen-before {@code Future.get()} returns.
 */
public class ExecutorSubmitGetEdgeDemo {

    private static int config;      // plain
    private static String report;   // plain

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            config = 21;                                   // before submit()

            Callable<Integer> task = () -> {
                report = "doubled " + config;              // sees 21 (submit edge)
                return config * 2;
            };
            Future<Integer> future = pool.submit(task);

            int result = future.get();                     // get() edge: report is visible
            System.out.println("result: " + result);
            System.out.println("report: " + report);
        } finally {
            pool.shutdown();
        }
    }
}
