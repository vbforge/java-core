package com.vbforge.practice.practice_4_copy_on_write_snapshot_iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteArrayList: every write (add, set, remove) copies the whole underlying array,
 * and an iterator works on a stable snapshot of the array taken when it was created.
 *
 * Use when:
 *   - reads vastly outnumber writes (listener lists, rarely-changing caches)
 *   - you want snapshot semantics (the iterator never sees later changes)
 *
 * Do NOT use when:
 *   - writes are frequent (O(n) copy on every write)
 *   - memory is a concern (two arrays exist during a write)
 */
public class CopyOnWriteSnapshotIteratorDemo {

    public static void main(String[] args) {

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("alpha", "beta", "gamma"));

        // The iterator takes a snapshot: safe to iterate without external synchronization
        Iterator<String> snapshot = list.iterator();
        list.add("delta"); // the write creates a new array; the snapshot is unaffected

        System.out.print("Snapshot (taken before add): ");
        while (snapshot.hasNext()) {
            System.out.print(snapshot.next() + " ");
        }
        System.out.println();
        System.out.println("Current list (after add): " + list);
        // snapshot: [alpha, beta, gamma]; list: [alpha, beta, gamma, delta]

        // Modifying the LIST inside a for-each does not throw ConcurrentModificationException,
        // because the loop iterates over a snapshot
        for (String s : list) {
            if (s.equals("beta")) {
                list.remove("beta");
            }
        }
        System.out.println("After remove inside for-each (no CME): " + list);
    }
}
