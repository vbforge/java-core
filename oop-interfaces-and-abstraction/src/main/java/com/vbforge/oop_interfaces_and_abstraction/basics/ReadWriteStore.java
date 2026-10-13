package com.vbforge.oop_interfaces_and_abstraction.basics;

/**
 * An interface may extend SEVERAL interfaces — this is "multiple inheritance of type".
 * It declares nothing new: it just combines two capabilities under one name.
 */
public interface ReadWriteStore extends ReadableStore, WritableStore {
}
