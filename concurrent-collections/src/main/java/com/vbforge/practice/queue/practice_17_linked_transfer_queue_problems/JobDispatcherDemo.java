package com.vbforge.practice.queue.practice_17_linked_transfer_queue_problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Thread-pool-like job dispatcher.
 * Workers wait on a LinkedTransferQueue; the dispatcher hands each job over with transfer(),
 * which returns only once a worker has taken it.
 * Shutdown uses one poison pill per worker (N consumers need N pills).
 */
public class JobDispatcherDemo {

    private static final int WORKERS = 3;
    private static final int JOBS = 5;
    private static final Runnable POISON_PILL = () -> { };

    public static void main(String[] args) throws InterruptedException {

        LinkedTransferQueue<Runnable> queue = new LinkedTransferQueue<>();
        ConcurrentLinkedQueue<Integer> executedJobs = new ConcurrentLinkedQueue<>();

        // Consumer workers
        List<Thread> workers = new ArrayList<>();
        for (int i = 1; i <= WORKERS; i++) {
            Thread worker = new Thread(() -> {
                try {
                    while (true) {
                        Runnable job = queue.take();
                        if (job == POISON_PILL) {
                            break;
                        }
                        job.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-" + i);
            workers.add(worker);
            worker.start();
        }

        // Dispatch jobs
        for (int j = 1; j <= JOBS; j++) {
            int jobId = j;
            queue.transfer(() -> executedJobs.add(jobId));
        }

        // One pill per worker
        for (int i = 0; i < WORKERS; i++) {
            queue.put(POISON_PILL);
        }
        for (Thread worker : workers) {
            worker.join();
        }

        // which worker ran which job is up to the scheduler, so print an order-independent result
        List<Integer> sorted = new ArrayList<>(executedJobs);
        Collections.sort(sorted);
        System.out.println("Executed jobs: " + sorted);
    }
}
