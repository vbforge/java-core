package com.vbforge.virtual_threads_intro;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 🎯 Tasks for virtual-thread basics. Read {@link VirtualThreadBasicsDemo} first.
 *
 * <p>Each method currently throws {@link UnsupportedOperationException}.
 * Implement it, then remove {@code @Disabled} from the matching nested class in
 * {@code VirtualThreadBasicsTasksTest}.
 */
public class VirtualThreadBasicsTasks {

    /**
     * Starts ONE virtual thread with the given name. Inside that thread, record
     * {@code <thread name>:<isVirtual>}. Wait for the thread to finish and return the record.
     *
     * <p>Examples:
     * <pre>
     * runOnVirtualThread("worker-1")  → "worker-1:true"
     * runOnVirtualThread("")          → ":true"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The {@code Thread} builder lets you set a name before starting</li>
     *   <li>A lambda cannot return a value — you need somewhere to put the result</li>
     *   <li>Make sure the result is visible to the calling thread after the virtual thread ends</li>
     * </ul>
     *
     * @param name the name of the virtual thread (may be empty)
     * @return {@code "<name>:true"}, recorded from inside the virtual thread
     */
    public String runOnVirtualThread(String name) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement runOnVirtualThread");
    }

    /**
     * Squares every number, with each square computed in its OWN virtual thread.
     * The result keeps the order of the input.
     *
     * <p>Examples:
     * <pre>
     * squaresOnVirtualThreads([1, 2, 3])   → [1, 4, 9]
     * squaresOnVirtualThreads([-3, 0])     → [9, 0]
     * squaresOnVirtualThreads([])          → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One executor in a try-with-resources block owns all the tasks</li>
     *   <li>{@code submit} gives you something you can ask for the result later</li>
     *   <li>Collect the results in the same order you submitted</li>
     * </ul>
     *
     * @param numbers input values (small enough that {@code n * n} fits in an int)
     * @return the squares, in input order
     */
    public List<Integer> squaresOnVirtualThreads(List<Integer> numbers)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement squaresOnVirtualThreads");
    }

    /**
     * Runs {@code taskCount} tasks on a virtual-thread-per-task executor. Every task records
     * {@code Thread.currentThread()} (the {@code Thread} object itself, not its name).
     * Return how many DIFFERENT threads were used.
     *
     * <p>Examples:
     * <pre>
     * countDistinctThreads(100)  → 100   (virtual threads are never reused)
     * countDistinctThreads(1)    → 1
     * countDistinctThreads(0)    → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use a thread-safe set — many threads write to it at the same time</li>
     *   <li>Read the set only after every task has finished</li>
     * </ul>
     *
     * @param taskCount number of tasks to run (&gt;= 0)
     * @return the number of distinct threads that ran the tasks
     */
    public int countDistinctThreads(int taskCount) {
        throw new UnsupportedOperationException("TODO: implement countDistinctThreads");
    }

    /**
     * Creates — but does NOT start — a virtual thread with the given name that will run
     * {@code body} once started.
     *
     * <p>Examples:
     * <pre>
     * Thread t = createUnstartedVirtual("job", body);
     * t.getState()   → NEW
     * t.isVirtual()  → true
     * t.getName()    → "job"
     * t.start();     → body runs exactly once
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The builder has a method for "create but do not start"</li>
     * </ul>
     *
     * @param name the thread name
     * @param body the code to run when the thread is started
     * @return a new, unstarted virtual thread
     */
    public Thread createUnstartedVirtual(String name, Runnable body) {
        throw new UnsupportedOperationException("TODO: implement createUnstartedVirtual");
    }
}
