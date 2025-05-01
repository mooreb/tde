package com.mooreb.tde.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectCounter<T> {
    private final Map<T, Long> underlyingMap = new HashMap<>();

    public ObjectCounter() {}

    public void add(T t) {
        if(underlyingMap.containsKey(t)) {
            underlyingMap.put(t, 1 + underlyingMap.get(t));
        }
        else {
            underlyingMap.put(t, 1L);
        }
    }

    public class Entry implements Comparable<Entry> {
        private final T t;
        private final long count;

        public Entry(T t, long count) {
            this.t = t;
            this.count = count;
        }

        @Override
        public int compareTo(Entry o) {
            return -Long.compare(this.count, o.count);
        }

        public T getValue() {
            return t;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "t=" + t +
                    ", count=" + count +
                    '}';
        }
    }

    public List<Entry> sortByCount() {
        final List<Entry> retval = new ArrayList<>(underlyingMap.size());
        for(final Map.Entry<T, Long> entry : underlyingMap.entrySet()) {
            final long count = entry.getValue();
            final T t = entry.getKey();
            retval.add(new Entry(t, count));
        }
        Collections.sort(retval);
        return retval;
    }

    public T getMostFrequentValue() {
        final Entry mostFrequentEntry = getMostFrequentEntry();
        if(null == mostFrequentEntry) return null;
        return mostFrequentEntry.getValue();
    }

    public Entry getMostFrequentEntry() {
        final List<Entry> entries = sortByCount();
        if(entries.isEmpty()) {
            return null;
        }
        else {
            return entries.get(0);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final List<Entry> entries = sortByCount();
        sb.append("{ObjectCounter: " + entries.size() + " entries: [\n");
        for(final Entry entry : entries) {
            sb.append("  ").append(entry).append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
}
