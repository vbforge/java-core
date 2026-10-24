package com.vbforge.collections_internals_and_big_o.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A stripped-down re-implementation of {@link java.util.HashMap}'s core mechanics, with
 * inspection methods that make the hidden structure visible.
 *
 * <p>Mirrored from the real class:
 * <ul>
 *   <li>the table length is always a POWER OF TWO (default 16)</li>
 *   <li>hash spreading: {@code h ^ (h >>> 16)}</li>
 *   <li>bucket index: {@code spread(hash) & (tableLength - 1)}; a {@code null} key has hash 0</li>
 *   <li>collisions form a linked chain; a new node is appended at the TAIL</li>
 *   <li>when {@code size > capacity * 0.75} the table DOUBLES and every entry is redistributed,
 *       keeping the relative order inside a bucket</li>
 *   <li>the table is allocated lazily on the first {@code put}</li>
 * </ul>
 *
 * <p>NOT mirrored: the real map turns a chain of 9+ nodes into a red-black tree. This class always keeps chains —
 * which is exactly what lets it show how costly long chains are.
 */
public class MiniHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        private Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }

    private final int initialCapacity;
    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private int resizeCount;

    public MiniHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public MiniHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        this.initialCapacity = tableSizeFor(initialCapacity);
    }

    /** Smallest power of two that is at least {@code capacity} (minimum 1). */
    public static int tableSizeFor(int capacity) {
        int n = 1;
        while (n < capacity && n < MAXIMUM_CAPACITY) {
            n <<= 1;
        }
        return n;
    }

    /** Mixes the high 16 bits into the low 16 bits, because the bucket index only looks at the low bits. */
    public static int spread(int hashCode) {
        return hashCode ^ (hashCode >>> 16);
    }

    private static int hash(Object key) {
        return key == null ? 0 : spread(key.hashCode());
    }

    public V put(K key, V value) {
        int hash = hash(key);
        if (table == null) {
            allocate();
        }
        int index = hash & (table.length - 1);
        Node<K, V> node = table[index];
        if (node == null) {
            table[index] = new Node<>(hash, key, value);
        } else {
            while (true) {
                if (node.hash == hash && Objects.equals(key, node.key)) {
                    V old = node.value;
                    node.value = value;
                    return old;
                }
                if (node.next == null) {
                    break;
                }
                node = node.next;
            }
            node.next = new Node<>(hash, key, value);
        }
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    public V get(Object key) {
        Node<K, V> node = find(key);
        return node == null ? null : node.value;
    }

    public boolean containsKey(Object key) {
        return find(key) != null;
    }

    public int size() {
        return size;
    }

    /** Length of the bucket array; 0 until the first {@code put}. */
    public int tableSize() {
        return table == null ? 0 : table.length;
    }

    /** How many times the table has doubled (the first allocation does not count). */
    public int resizeCount() {
        return resizeCount;
    }

    /** The bucket a key belongs to in the CURRENT table size. */
    public int bucketIndexOf(Object key) {
        int length = table == null ? initialCapacity : table.length;
        return hash(key) & (length - 1);
    }

    /** Length of the longest collision chain. */
    public int longestChain() {
        int longest = 0;
        if (table != null) {
            for (Node<K, V> head : table) {
                int length = 0;
                for (Node<K, V> n = head; n != null; n = n.next) {
                    length++;
                }
                longest = Math.max(longest, length);
            }
        }
        return longest;
    }

    /** How many nodes a lookup of {@code key} has to examine (a miss examines the whole chain). */
    public int probesForGet(Object key) {
        if (table == null) {
            return 0;
        }
        int hash = hash(key);
        int probes = 0;
        for (Node<K, V> n = table[hash & (table.length - 1)]; n != null; n = n.next) {
            probes++;
            if (n.hash == hash && Objects.equals(key, n.key)) {
                break;
            }
        }
        return probes;
    }

    /** The keys in iteration order: bucket by bucket, each chain from head to tail. */
    public List<K> keysInIterationOrder() {
        List<K> keys = new ArrayList<>();
        if (table != null) {
            for (Node<K, V> head : table) {
                for (Node<K, V> n = head; n != null; n = n.next) {
                    keys.add(n.key);
                }
            }
        }
        return keys;
    }

    private Node<K, V> find(Object key) {
        if (table == null) {
            return null;
        }
        int hash = hash(key);
        for (Node<K, V> n = table[hash & (table.length - 1)]; n != null; n = n.next) {
            if (n.hash == hash && Objects.equals(key, n.key)) {
                return n;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void allocate() {
        table = (Node<K, V>[]) new Node[initialCapacity];
        threshold = (int) (initialCapacity * LOAD_FACTOR);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length << 1;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        for (Node<K, V> head : oldTable) {
            for (Node<K, V> n = head; n != null; ) {
                Node<K, V> next = n.next;
                n.next = null;
                append(newTable, n);
                n = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
        resizeCount++;
    }

    private void append(Node<K, V>[] target, Node<K, V> node) {
        int index = node.hash & (target.length - 1);
        if (target[index] == null) {
            target[index] = node;
        } else {
            Node<K, V> tail = target[index];
            while (tail.next != null) {
                tail = tail.next;
            }
            tail.next = node;
        }
    }
}
