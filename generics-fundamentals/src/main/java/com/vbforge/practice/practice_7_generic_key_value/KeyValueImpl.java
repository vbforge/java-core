package com.vbforge.practice.practice_7_generic_key_value;

public class KeyValueImpl<K, V> implements KeyValue<K, V> {
    private final K key;
    private final V value;

    public KeyValueImpl(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }
}
