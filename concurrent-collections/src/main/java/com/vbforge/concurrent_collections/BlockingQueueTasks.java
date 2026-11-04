package com.vbforge.concurrent_collections;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * Tasks for {@code BlockingQueueDemo}. Implement each method, then remove
 * {@code @Disabled} from the matching {@code @Nested} class in
 * {@code BlockingQueueTasksTest}.
 *
 * <p>Each test runs with a 10 second time-out, so a deadlock in your solution
 * shows up as a failed test and not as a build that never finishes.
 */
public class BlockingQueueTasks {

    /**
     * Add up {@code numbers} with a classic producer / consumer pair.
     *
     * <p>A <b>producer thread</b> puts every number into a bounded queue of
     * capacity {@code queueCapacity}. The <b>calling thread</b> is the consumer:
     * it takes numbers from the queue and sums them. The producer must tell
     * the consumer when it is finished by sending a poison pill.
     *
     * <p>Examples:
     * <pre>
     * sumWithProducerConsumer([1, 2, 3, 4, 5], 2)   →  15
     * sumWithProducerConsumer([], 3)                →  0
     * sumWithProducerConsumer([7], 1)               →  7
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A bounded queue whose capacity is fixed at construction time</li>
     *   <li>The blocking insert and the blocking remove are the methods that wait</li>
     *   <li>All numbers are zero or positive, so a negative number can never be real data</li>
     *   <li>The sum can exceed {@code Integer.MAX_VALUE}</li>
     * </ul>
     *
     * @param numbers       the numbers to add up (never null, all elements &gt;= 0)
     * @param queueCapacity capacity of the queue between producer and consumer, at least 1
     * @return the sum of all numbers
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public long sumWithProducerConsumer(List<Integer> numbers, int queueCapacity) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement sumWithProducerConsumer");
    }

    /**
     * Remove up to {@code max} elements that are available <b>right now</b>
     * from {@code queue}, without waiting, and return them in queue order.
     * Elements that are not taken stay in the queue.
     *
     * <p>Examples:
     * <pre>
     * queue = [1, 2, 3, 4, 5]
     * drainBatch(queue, 3)   →  [1, 2, 3]      queue is now [4, 5]
     * drainBatch(queue, 10)  →  [4, 5]         queue is now []
     * drainBatch(queue, 10)  →  []             returns immediately, does not wait
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code BlockingQueue} has a bulk-transfer method that takes a target collection and a maximum</li>
     * </ul>
     *
     * @param queue the queue to take from (never null)
     * @param max   the maximum number of elements to take, at least 0
     * @return the removed elements, in the order they were queued
     */
    public List<Integer> drainBatch(BlockingQueue<Integer> queue, int max) {
        throw new UnsupportedOperationException("TODO: implement drainBatch");
    }

    /**
     * Wait at most {@code timeoutMillis} milliseconds for an element and return
     * it, or return an empty {@link Optional} if none arrived in time. The
     * element is removed from the queue.
     *
     * <p>Examples:
     * <pre>
     * queue = ["a"]
     * pollWithTimeout(queue, 100)   →  Optional.of("a")      returns at once
     *
     * queue = []
     * pollWithTimeout(queue, 50)    →  Optional.empty        returns after about 50 ms
     *
     * queue = []   (another thread adds "b" after 30 ms)
     * pollWithTimeout(queue, 5000)  →  Optional.of("b")      returns as soon as "b" arrives
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Of the four flavours of "remove", this is the one that waits but gives up after a deadline</li>
     *   <li>It needs a {@code TimeUnit}</li>
     * </ul>
     *
     * @param queue         the queue to take from (never null)
     * @param timeoutMillis the maximum time to wait, in milliseconds, at least 0
     * @return the element, or {@link Optional#empty()}
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public Optional<String> pollWithTimeout(BlockingQueue<String> queue, long timeoutMillis)
            throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement pollWithTimeout");
    }

    /**
     * Return the job names ordered by priority: the job with the <b>lowest</b>
     * priority number comes first. Serve the jobs through a priority-ordered
     * blocking queue.
     *
     * <p>All priorities in one call are distinct.
     *
     * <p>Examples:
     * <pre>
     * serveByPriority({low=5, high=1, mid=3})   →  [high, mid, low]
     * serveByPriority({only=9})                 →  [only]
     * serveByPriority({})                       →  []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The priority queue of the {@code BlockingQueue} family needs a {@code Comparator} for
     *       elements that are not naturally ordered</li>
     *   <li>Its initial capacity must be at least 1, even for an empty map</li>
     *   <li>Only {@code poll} / {@code take} respect the priority - iterating does not</li>
     * </ul>
     *
     * @param jobPriorities job name → priority (never null, priorities are distinct)
     * @return the job names, lowest priority number first
     */
    public List<String> serveByPriority(Map<String, Integer> jobPriorities) {
        throw new UnsupportedOperationException("TODO: implement serveByPriority");
    }

    /**
     * Process {@code jobs} with {@code consumers} worker threads that share ONE
     * queue. Processing a job means converting it to upper case. Return the
     * processed jobs (in any order) after <b>all</b> workers have stopped.
     *
     * <p>The calling thread is the producer: it puts every job into the queue and
     * then tells the workers to stop. No worker may be left waiting forever.
     *
     * <p>Jobs are never null and never empty.
     *
     * <p>Examples:
     * <pre>
     * processInParallel(["a", "b", "c"], 2)   →  ["A", "B", "C"]   (any order)
     * processInParallel(["x", "x", "y"], 3)   →  ["X", "X", "Y"]   (any order)
     * processInParallel([], 4)                →  []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One poison pill per consumer - a consumer that takes a pill does not put it back</li>
     *   <li>An empty string can never be a real job, so it can be the pill</li>
     *   <li>The results are collected by several threads at once - use a thread-safe collection</li>
     * </ul>
     *
     * @param jobs      the jobs to process (never null)
     * @param consumers number of worker threads, at least 1
     * @return the upper-cased jobs, in any order
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public List<String> processInParallel(List<String> jobs, int consumers) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement processInParallel");
    }
}
