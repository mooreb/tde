package com.mooreb.tde.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImmutableMultiMap<K, V> implements MultiMap<K,V> {
    private final MultiMap<K, V> delegate;

    public ImmutableMultiMap(MultiMap<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(K k, V v) {
        throw new UnsupportedOperationException("cannot modify an ImmutableMultiMap");
    }

    @Override
    public void place(K k, List<V> vs) {
        throw new UnsupportedOperationException("cannot modify an ImmutableMultiMap");
    }

    @Override
    public List<V> get(K k) {
        return delegate.get(k);
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Set<Map.Entry<K, List<V>>> entries() {
        return delegate.entries();
    }

    @Override
    public Collection<List<V>> values() {
        return delegate.values();
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableMultiMap<?, ?> that = (ImmutableMultiMap<?, ?>) o;

        return delegate != null ? delegate.equals(that.delegate) : that.delegate == null;
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ImmutableMultiMap{" +
                "delegate=" + delegate +
                '}';
    }
}
