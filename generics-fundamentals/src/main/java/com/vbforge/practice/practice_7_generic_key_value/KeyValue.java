package com.vbforge.practice.practice_7_generic_key_value;

public interface KeyValue<K, V> {
    K getKey();

    V getValue();
}
