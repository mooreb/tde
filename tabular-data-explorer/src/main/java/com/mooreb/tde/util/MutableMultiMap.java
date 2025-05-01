package com.mooreb.tde.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MutableMultiMap<K,V> implements MultiMap<K, V> {
    private final Map<K,List<V>> internalMap = new HashMap<>();

    @Override
    public void put(K k, V v) {
        List<V> list = internalMap.get(k);
        if(null == list) {
            list = new ArrayList<V>();
            internalMap.put(k, list);
        }
        list.add(v);
    }

    @Override
    public void place(K k, List<V> vs) {
        internalMap.put(k, vs);
    }

    @Override
    public List<V> get(K k) {
        final List<V> fromDelegate = internalMap.get(k);
        if(null == fromDelegate) return null;
        return fromDelegate;
    }

    @Override
    public Set<K> keySet() {
        final Set<K> fromDelegate = internalMap.keySet();
        if(null == fromDelegate) return null;
        return Collections.unmodifiableSet(fromDelegate);
    }

    @Override
    public Set<Map.Entry<K,List<V>>> entries() {
        final Set<Map.Entry<K, List<V>>> fromDelegate = internalMap.entrySet();
        if(null == fromDelegate) return null;
        return Collections.unmodifiableSet(fromDelegate);
    }

    @Override
    public Collection<List<V>> values() {
        final Collection<List<V>> fromDelegate = internalMap.values();
        if(null == fromDelegate) return null;
        return Collections.unmodifiableCollection(fromDelegate);
    }

    /** Count the total number of entries in this multimap;
     *
     * @return the sum of the length of all the lists in the map
     */
    @Override
    public long count() {
        long retval = 0;
        for(List<V> v : internalMap.values()) {
            retval += v.size();
        }
        return retval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutableMultiMap<?, ?> that = (MutableMultiMap<?, ?>) o;

        return internalMap != null ? internalMap.equals(that.internalMap) : that.internalMap == null;
    }

    @Override
    public int hashCode() {
        return internalMap != null ? internalMap.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Set<K> keySet = internalMap.keySet();

        sb.append("MutableMultiMap{");

        for(final K k : keySet()) {
            final List<V> vs = internalMap.get(k);
            sb.append(k);
            sb.append("[");
            sb.append(vs.size());
            sb.append("]");
            sb.append(": ");
            final Iterator<V> iterator = vs.iterator();
            while(iterator.hasNext()) {
                V v = iterator.next();
                sb.append(v);
                if(iterator.hasNext()) {
                    sb.append(", ");
                }
                else {
                    sb.append("\n");
                }
            }
        }

        sb.append("}");

        return sb.toString();
    }
}
