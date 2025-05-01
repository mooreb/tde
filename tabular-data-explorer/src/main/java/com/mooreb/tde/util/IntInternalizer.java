package com.mooreb.tde.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class IntInternalizer<T> {
    private final ConcurrentHashMap<T, Integer> internalMap = new ConcurrentHashMap<>();
    private final List<T> internalList = new ArrayList<>();
    private int currentIndex = 0;

    public IntInternalizer() {}

    public IntInternalizer(Collection<? extends T> collection) {
        addAll(collection);
    }

    public IntInternalizer(final T[] ts) {
        addAll(ts);
    }

    public synchronized int add(final T t) {
        final Integer i = internalMap.get(t);
        if(null != i) {
            return i;
        }
        else {
            internalList.add(t);
            final Integer previousValue = internalMap.putIfAbsent(t, currentIndex);
            if(null != previousValue) throw new ConcurrentModificationException();
            return currentIndex++;
        }
    }

    public synchronized void addAll(final T[] ts) {
        for(int i=0; i<ts.length; i++) {
            add(ts[i]);
        }
    }

    public synchronized void addAll(Collection<? extends T> collection) {
        for (T t : collection) {
            add(t);
        }
    }

    public int getIntFor(final T t) {
        return getIntFor(t, false);
    }

    public int getIntFor(final T t, boolean addIfAbsent) {
        final Integer i = internalMap.get(t);
        if (null == i) {
            if(addIfAbsent) {
                return add(t);
            }
            else {
                throw new NoSuchElementException();
            }
        } else {
            return i;
        }
    }

    public T getInternedObjectFor(int i) {
        return internalList.get(i);
    }

    public Map<T, Integer> getAllOffsets() {
        return Collections.unmodifiableMap(internalMap);
    }

}
