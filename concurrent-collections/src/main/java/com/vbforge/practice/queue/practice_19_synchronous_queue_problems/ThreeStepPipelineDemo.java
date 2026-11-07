package com.vbforge.practice.queue.practice_19_synchronous_queue_problems;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Pipeline step control.
 * Goal: data flows through three threads in lock-step, each stage handing its result to the next
 * through a SynchronousQueue (a stage can only finish once the next stage has taken the data).
 */
public class ThreeStepPipelineDemo {

    public static void main(String[] args) throws InterruptedException {

        SynchronousQueue<String> stage1 = new SynchronousQueue<>();
        SynchronousQueue<String> stage2 = new SynchronousQueue<>();

        AtomicReference<String> sentByStage1 = new AtomicReference<>();
        AtomicReference<String> producedByStage2 = new AtomicReference<>();
        AtomicReference<String> receivedByStage3 = new AtomicReference<>();

        Thread t1 = new Thread(() -> {
            try {
                stage1.put("raw-data");
                sentByStage1.set("raw-data");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                String data = stage1.take();
                String processed = data.toUpperCase();
                stage2.put(processed);
                producedByStage2.set(processed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                receivedByStage3.set(stage2.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        // the stages run on different threads, so report after join (stable order)
        System.out.println("Stage 1 sent: " + sentByStage1.get());
        System.out.println("Stage 2 produced: " + producedByStage2.get());
        System.out.println("Final Output: " + receivedByStage3.get());
    }
}
