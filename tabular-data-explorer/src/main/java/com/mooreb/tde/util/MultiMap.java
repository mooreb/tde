package com.mooreb.tde.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MultiMap<K, V> {
    void put(K k, V v);

    void place(K k, List<V> vs);

    List<V> get(K k);

    Set<K> keySet();

    Set<Map.Entry<K,List<V>>> entries();

    Collection<List<V>> values();

    long count();

    @Override
    String toString();
}
