package com.mooreb.tde.util.iom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutableObjectMemoizer<T> {
    private final Map<T, String> objectToNameMap = new HashMap<>();
    private final Map<String, T> nameToObjectMap = new HashMap<>();
    private final String namePrefix;
    private int currentNameIndex = 0;
    private final boolean mayBeModified;


    public ImmutableObjectMemoizer(final String namePrefix) {
        this.namePrefix = namePrefix;
        this.mayBeModified = true;
    }

    public ImmutableObjectMemoizer(final Map<String, T> nameToValueMap) {
        for (final String name : nameToValueMap.keySet()) {
            final T t = nameToValueMap.get(name);
            recordNameForObject(name, t);
        }
        this.namePrefix = null;
        this.mayBeModified = false;
    }

    public T add(T t) {
        if (!mayBeModified)
            throw new IllegalStateException("cannot modify a ImmutableObjectMemoizer constructed this way.");
        if (objectToNameMap.containsKey(t)) {
            return t;
        } else {
            final String name = String.format("%s-%05d", namePrefix, ++currentNameIndex);
            objectToNameMap.put(t, name);
            nameToObjectMap.put(name, t);
            return t;
        }
    }

    public T getObjectNamed(final String name) {
        return nameToObjectMap.get(name);
    }

    public String getNameForObject(final T t) {
        return objectToNameMap.get(t);
    }

    public List<NameValuePair<T>> getNameValuePairs() {
        List<NameValuePair<T>> retval = new ArrayList<>();
        for (Map.Entry<String, T> entry : nameToObjectMap.entrySet()) {
            final String name = entry.getKey();
            final T value = entry.getValue();
            final NameValuePair<T> nameValuePair = new NameValuePair<T>(name, value);
            retval.add(nameValuePair);
        }
        Collections.sort(retval, new Comparator<NameValuePair<T>>() {
            @Override
            public int compare(NameValuePair<T> o1, NameValuePair<T> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return Collections.unmodifiableList(retval);
    }

    private void recordNameForObject(final String name, final T t) {
        if (nameToObjectMap.containsKey(name))
            throw new IllegalArgumentException("cannot record name " + name + " for object " + t + " as the name already exists");
        if (objectToNameMap.containsKey(t))
            throw new IllegalArgumentException("cannot record name " + name + " for object " + t + " as the object already exists");
        nameToObjectMap.put(name, t);
        objectToNameMap.put(t, name);
    }
}
