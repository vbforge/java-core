package com.vbforge.practice.queue.practice_18_synchronous_queue_basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Thread handoff coordination.
 * Two workers wait on a SynchronousQueue; each put() from main hands one job to exactly one waiting worker.
 */
public class HandoffBetweenThreadsDemo {

    public static void main(String[] args) throws InterruptedException {

        SynchronousQueue<String> handoffQueue = new SynchronousQueue<>();
        ConcurrentLinkedQueue<String> receivedJobs = new ConcurrentLinkedQueue<>();

        Runnable worker = () -> {
            try {
                String job = handoffQueue.take(); // wait for a job
                receivedJobs.add(job);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread worker1 = new Thread(worker, "Worker-1");
        Thread worker2 = new Thread(worker, "Worker-2");
        worker1.start();
        worker2.start();

        handoffQueue.put("Job-A");
        handoffQueue.put("Job-B");

        worker1.join();
        worker2.join();

        // which worker got which job is up to the scheduler, so print the jobs sorted
        List<String> jobs = new ArrayList<>(receivedJobs);
        Collections.sort(jobs);
        System.out.println("Jobs handed off: " + jobs);
    }
}
