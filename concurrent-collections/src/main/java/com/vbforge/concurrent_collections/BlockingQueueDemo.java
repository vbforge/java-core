package com.vbforge.concurrent_collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Demo: the {@link BlockingQueue} family — queues that make threads WAIT instead
 * of failing, which is the foundation of the producer / consumer pattern.
 *
 * <p>Every claim in a comment is either printed by {@link #main(String[])} or
 * asserted in {@code BlockingQueueDemoTest}. Sections that involve waiting use
 * tiny timeouts (10-100 ms) or wait for a thread to reach a known state, so the
 * whole demo runs in well under a second.
 */
public class BlockingQueueDemo {

    /** Sentinel that tells a consumer "no more work is coming". */
    private static final int POISON_PILL = -1;

    public static void main(String[] args) throws InterruptedException {
        operationFlavoursDemo();
        System.out.println("---");
        capacityAndNullsDemo();
        System.out.println("---");
        producerConsumerDemo();
        System.out.println("---");
        backPressureDemo();
        System.out.println("---");
        multipleConsumersDemo();
        System.out.println("---");
        drainToDemo();
        System.out.println("---");
        priorityBlockingQueueDemo();
        System.out.println("---");
        synchronousQueueDemo();
        System.out.println("---");
        delayQueueDemo();
        System.out.println("---");
        interruptionDemo();
    }

    // =========================================================================
    // 1. Four flavours of every operation
    // =========================================================================

    /*
     * WHAT
     *   A BlockingQueue offers each of insert / remove / examine in four
     *   flavours, which differ in what happens when the queue is full (insert)
     *   or empty (remove / examine):
     *
     *                 throws exception   special value   blocks    times out
     *     insert      add(e)             offer(e)        put(e)    offer(e, time, unit)
     *     remove      remove()           poll()          take()    poll(time, unit)
     *     examine     element()          peek()          -         -
     *
     * WHY
     *   Different callers need different behaviour: a worker that has nothing
     *   to do should block (take); a UI thread must never block (poll); a
     *   request handler should give up after a deadline (offer / poll with a
     *   timeout); and "this must never happen" code can use the exception forms.
     *
     * COMPLEXITY
     *   - all of the non-blocking operations: O(1) on ArrayBlockingQueue
     *
     * GOTCHA
     *   add() on a full queue THROWS IllegalStateException - it does not wait.
     *   People new to the API reach for add() out of habit from List.add() and
     *   are surprised. put() is the one that waits.
     */
    public static void operationFlavoursDemo() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        System.out.println("poll() on empty            -> " + queue.poll());
        System.out.println("peek() on empty            -> " + queue.peek());
        System.out.println("poll(10 ms) on empty       -> " + queue.poll(10, TimeUnit.MILLISECONDS));
        try {
            queue.remove();
        } catch (NoSuchElementException e) {
            System.out.println("remove() on empty          -> NoSuchElementException");
        }
        try {
            queue.element();
        } catch (NoSuchElementException e) {
            System.out.println("element() on empty         -> NoSuchElementException");
        }

        queue.add("first");
        System.out.println("offer() on full            -> " + queue.offer("second"));
        System.out.println("offer(10 ms) on full       -> " + queue.offer("second", 10, TimeUnit.MILLISECONDS));
        try {
            queue.add("second");
        } catch (IllegalStateException e) {
            System.out.println("add() on full              -> IllegalStateException");
        }
        System.out.println("peek() does not remove     -> " + queue.peek() + ", size = " + queue.size());
    }

    // =========================================================================
    // 2. Capacity and nulls
    // =========================================================================

    /*
     * WHAT
     *   The JDK ships several BlockingQueue implementations. The first thing to
     *   know about each is its CAPACITY (remainingCapacity() tells you):
     *     ArrayBlockingQueue(n)     bounded, capacity fixed at construction
     *     LinkedBlockingQueue()     "unbounded" - really Integer.MAX_VALUE
     *     LinkedBlockingQueue(n)    bounded
     *     PriorityBlockingQueue     unbounded, ordered by priority (section 7)
     *     SynchronousQueue          capacity ZERO - a hand-off point (section 8)
     *     DelayQueue                unbounded, elements become available after a delay (section 9)
     *   None of them accepts null: null is reserved as poll()'s "nothing there".
     *
     * WHY
     *   BOUNDED queues give you back-pressure (section 4). An unbounded queue
     *   never makes a fast producer wait, so a slow consumer lets it grow until
     *   the JVM runs out of memory.
     *
     * COMPLEXITY
     *   - ArrayBlockingQueue: O(1) put/take, one lock shared by producers and consumers
     *   - LinkedBlockingQueue: O(1) put/take, separate locks for the head and the tail
     *
     * GOTCHA
     *   new LinkedBlockingQueue<>() is NOT bounded. Pass a capacity if you
     *   want back-pressure.
     */
    public static void capacityAndNullsDemo() throws InterruptedException {
        System.out.println("ArrayBlockingQueue(3)      -> remainingCapacity = "
                + new ArrayBlockingQueue<String>(3).remainingCapacity());
        System.out.println("LinkedBlockingQueue()      -> remainingCapacity = "
                + new LinkedBlockingQueue<String>().remainingCapacity());
        System.out.println("LinkedBlockingQueue(2)     -> remainingCapacity = "
                + new LinkedBlockingQueue<String>(2).remainingCapacity());
        System.out.println("PriorityBlockingQueue      -> remainingCapacity = "
                + new PriorityBlockingQueue<Integer>().remainingCapacity());
        System.out.println("SynchronousQueue           -> remainingCapacity = "
                + new SynchronousQueue<String>().remainingCapacity());

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        try {
            queue.put(null);
        } catch (NullPointerException e) {
            System.out.println("put(null)                  -> NullPointerException");
        }
        try {
            queue.offer(null);
        } catch (NullPointerException e) {
            System.out.println("offer(null)                -> NullPointerException");
        }
    }

    // =========================================================================
    // 3. Producer / consumer with a poison pill
    // =========================================================================

    /*
     * WHAT
     *   One thread produces items with put(), another consumes them with
     *   take(). When the producer is done it puts a special POISON PILL that
     *   tells the consumer to stop.
     *
     * WHY
     *   The queue is the ONLY thing the two threads share, and it does all the
     *   locking and waiting for them. No synchronized, no wait/notify.
     *   The poison pill solves "how does a consumer that blocks forever in
     *   take() know there will be no more work?".
     *
     * COMPLEXITY
     *   - O(1) per item
     *
     * GOTCHA
     *   The pill must be a value that real items can never have (here: -1,
     *   real items are positive). And it must go through the queue, behind the
     *   real items, so that everything before it is still processed.
     */
    public static void producerConsumerDemo() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put(i);
                }
                queue.put(POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "producer");
        producer.start();

        int sum = 0;
        while (true) {
            int item = queue.take();
            if (item == POISON_PILL) {
                break;
            }
            sum += item;
        }
        producer.join();
        System.out.println("consumer summed 1..10      -> " + sum);
    }

    // =========================================================================
    // 4. Back-pressure
    // =========================================================================

    /*
     * WHAT
     *   When a bounded queue is full, put() parks the PRODUCER thread until a
     *   consumer makes room.
     *
     * WHY
     *   This is "back-pressure": a producer that is faster than its consumers
     *   is slowed down automatically, so memory use stays bounded.
     *
     * HOW THE DEMO WORKS
     *   A producer tries to put 5 items into a queue of capacity 2 while nobody
     *   consumes. We wait until the producer thread reports Thread.State.WAITING
     *   (parked inside put) - no sleeping and guessing - and only then start
     *   taking items.
     *
     * COMPLEXITY
     *   - a blocked put costs no CPU: the thread is parked
     *
     * GOTCHA
     *   Waiting for "the thread to be WAITING" is fine in a demo or a test; in
     *   production code you never need it - you simply call put().
     */
    public static void backPressureDemo() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "fast-producer");
        producer.start();

        awaitState(producer, Thread.State.WAITING);
        System.out.println("producer state             -> " + producer.getState()
                + ", queue size = " + queue.size());

        List<Integer> received = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            received.add(queue.take());
        }
        producer.join();
        System.out.println("consumer received          -> " + received);
    }

    // =========================================================================
    // 5. Several consumers, one pill each
    // =========================================================================

    /*
     * WHAT
     *   With N consumers sharing one queue, the producer puts N poison pills
     *   after the real work - one for each consumer.
     *
     * WHY
     *   A consumer that takes a pill stops, and it does not put the pill back.
     *   So you need exactly as many pills as consumers, or some consumer will
     *   wait in take() forever and the program never ends.
     *
     * COMPLEXITY
     *   - O(1) per item; items are shared out between consumers by whoever
     *     calls take() first
     *
     * GOTCHA
     *   The queue guarantees every item is taken by EXACTLY ONE consumer, but
     *   not WHICH one. Never write code that depends on which consumer gets
     *   which item.
     */
    public static void multipleConsumersDemo() throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        AtomicInteger processed = new AtomicInteger();
        int consumerCount = 3;

        Thread[] consumers = new Thread[consumerCount];
        for (int c = 0; c < consumerCount; c++) {
            consumers[c] = new Thread(() -> {
                try {
                    while (queue.take() != POISON_PILL) {
                        processed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "consumer-" + c);
            consumers[c].start();
        }

        for (int i = 1; i <= 100; i++) {
            queue.put(i);
        }
        for (int c = 0; c < consumerCount; c++) {
            queue.put(POISON_PILL);
        }
        for (Thread consumer : consumers) {
            consumer.join();
        }
        System.out.println("3 consumers, 100 items     -> processed " + processed.get()
                + ", queue left = " + queue.size());
    }

    // =========================================================================
    // 6. drainTo
    // =========================================================================

    /*
     * WHAT
     *   drainTo(collection) / drainTo(collection, max) removes the elements that
     *   are available RIGHT NOW and adds them to a collection, in one call.
     *   It never blocks and returns the number of elements transferred.
     *
     * WHY
     *   A consumer that processes in batches (write 100 rows to a database at
     *   once) would otherwise need a poll() loop - and each poll() takes the
     *   queue's lock again.
     *
     * COMPLEXITY
     *   - O(k) for k transferred elements
     *
     * GOTCHA
     *   drainTo on an empty queue returns 0 immediately - it does NOT wait for
     *   elements. If you want to wait for the first element and then take the
     *   rest, call take() first and drainTo() second.
     */
    public static void drainToDemo() {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        for (int i = 1; i <= 5; i++) {
            queue.add(i);
        }

        List<Integer> batch = new ArrayList<>();
        int first = queue.drainTo(batch, 3);
        System.out.println("drainTo(batch, 3)          -> moved " + first + ": " + batch
                + ", queue = " + queue);

        int rest = queue.drainTo(batch);
        System.out.println("drainTo(batch)             -> moved " + rest + ": " + batch
                + ", queue = " + queue);

        System.out.println("drainTo on empty queue     -> moved " + queue.drainTo(batch));
    }

    // =========================================================================
    // 7. PriorityBlockingQueue
    // =========================================================================

    /*
     * WHAT
     *   An UNBOUNDED blocking queue that hands out the "smallest" element first
     *   (natural ordering, or the Comparator you pass in) instead of the oldest.
     *
     * WHY
     *   Work that is not equally urgent: take() always returns the most
     *   important job that is currently queued.
     *
     * COMPLEXITY
     *   - offer / put: O(log n); poll / take: O(log n); peek: O(1)
     *
     * GOTCHA
     *   Only poll / take / peek honour the priority. Iterating the queue or
     *   printing it shows the internal heap order, which is NOT sorted. Also:
     *   it is unbounded, so put() never blocks - there is no back-pressure.
     */
    public static void priorityBlockingQueueDemo() throws InterruptedException {
        PriorityBlockingQueue<Integer> ascending = new PriorityBlockingQueue<>();
        ascending.add(5);
        ascending.add(1);
        ascending.add(3);
        System.out.println("natural order take()       -> " + ascending.take() + ", " + ascending.take()
                + ", " + ascending.take());

        PriorityBlockingQueue<Integer> descending =
                new PriorityBlockingQueue<>(11, Collections.<Integer>reverseOrder());
        descending.add(5);
        descending.add(1);
        descending.add(3);
        System.out.println("reverseOrder take()        -> " + descending.take() + ", " + descending.take()
                + ", " + descending.take());
    }

    // =========================================================================
    // 8. SynchronousQueue
    // =========================================================================

    /*
     * WHAT
     *   A queue with NO storage at all. put() waits until another thread calls
     *   take() (and vice versa): each insert is a direct hand-off.
     *
     * WHY
     *   It is the cheapest way to pass work from one thread to another when you
     *   want the sender to know the receiver has actually TAKEN it.
     *   Executors.newCachedThreadPool() uses one as its work queue.
     *
     * COMPLEXITY
     *   - size() is always 0; there is nothing to scan
     *
     * GOTCHA
     *   offer(e) returns false unless a consumer is ALREADY waiting in take();
     *   it does not "leave the element for later".
     */
    public static void synchronousQueueDemo() throws InterruptedException {
        SynchronousQueue<String> handOff = new SynchronousQueue<>();
        System.out.println("offer with no consumer     -> " + handOff.offer("lost?") + ", size = " + handOff.size());

        AtomicReference<String> received = new AtomicReference<>();
        Thread consumer = new Thread(() -> {
            try {
                received.set(handOff.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "receiver");
        consumer.start();

        handOff.put("hello");              // returns only after the consumer has taken it
        consumer.join();
        System.out.println("put() handed over         -> consumer got " + received.get());
    }

    // =========================================================================
    // 9. DelayQueue
    // =========================================================================

    /*
     * WHAT
     *   An unbounded queue of Delayed elements. An element can only be taken
     *   once its delay has EXPIRED, and the one that expires first comes out
     *   first. poll() returns null while nothing has expired; take() waits.
     *
     * WHY
     *   Scheduling without a timer thread of your own: retry-after-backoff,
     *   cache entry expiry, "send this reminder in 30 seconds".
     *
     * COMPLEXITY
     *   - offer: O(log n); take / poll: O(log n)
     *
     * GOTCHA
     *   Your element class must implement BOTH getDelay and compareTo, and the
     *   two must agree: the element with the smaller remaining delay must
     *   compare as smaller.
     */
    public static void delayQueueDemo() throws InterruptedException {
        DelayQueue<DelayedMessage> queue = new DelayQueue<>();
        queue.put(new DelayedMessage("late", 100));
        queue.put(new DelayedMessage("early", 50));

        System.out.println("poll() right away          -> " + queue.poll());
        System.out.println("take()                     -> " + queue.take().text());
        System.out.println("take()                     -> " + queue.take().text());
    }

    /** A message that becomes available {@code delayMillis} after it was created. */
    public static class DelayedMessage implements Delayed {
        private final String text;
        private final long availableAtNanos;

        public DelayedMessage(String text, long delayMillis) {
            this.text = text;
            this.availableAtNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(delayMillis);
        }

        public String text() {
            return text;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(availableAtNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            return Long.compare(availableAtNanos, ((DelayedMessage) other).availableAtNanos);
        }

        @Override
        public String toString() {
            return "DelayedMessage[" + text + "]";
        }
    }

    // =========================================================================
    // 10. Interruption
    // =========================================================================

    /*
     * WHAT
     *   put() and take() (and the timed versions) throw InterruptedException if
     *   the waiting thread is interrupted. That is how a blocked worker is
     *   asked to shut down.
     *
     * WHY
     *   A thread parked in take() cannot check a "stop" flag. Interrupting it
     *   is the standard way to wake it up.
     *
     * COMPLEXITY
     *   - not relevant here
     *
     * GOTCHA
     *   When InterruptedException is thrown, the thread's interrupt flag is
     *   CLEARED. If you catch the exception and do not rethrow it, call
     *   Thread.currentThread().interrupt() to restore the flag, or the code
     *   above you will never learn the thread was asked to stop.
     */
    public static void interruptionDemo() throws InterruptedException {
        BlockingQueue<String> emptyQueue = new ArrayBlockingQueue<>(1);
        AtomicBoolean caught = new AtomicBoolean();
        AtomicBoolean flagAfterCatch = new AtomicBoolean();
        AtomicBoolean flagAfterRestore = new AtomicBoolean();

        Thread worker = new Thread(() -> {
            try {
                emptyQueue.take();
            } catch (InterruptedException e) {
                caught.set(true);
                flagAfterCatch.set(Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
                flagAfterRestore.set(Thread.currentThread().isInterrupted());
            }
        }, "blocked-worker");
        worker.start();

        awaitState(worker, Thread.State.WAITING);
        worker.interrupt();
        worker.join();
        System.out.println("InterruptedException caught -> " + caught.get());
        System.out.println("interrupt flag after catch  -> " + flagAfterCatch.get());
        System.out.println("interrupt flag after restore-> " + flagAfterRestore.get());
    }

    // =========================================================================
    // Helper: wait (bounded) until a thread reaches a state
    // =========================================================================

    private static void awaitState(Thread thread, Thread.State expected) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (thread.getState() != expected) {
            if (System.nanoTime() > deadline) {
                throw new IllegalStateException(thread.getName() + " never reached " + expected);
            }
            Thread.sleep(1);
        }
    }
}
