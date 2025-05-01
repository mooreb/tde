package com.mooreb.tde.util;

import com.mooreb.tde.util.spreadsheet.MutableInMemorySpreadsheet;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUtils<T> {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static SetUtils<String> STRING = new SetUtils<String>();

    public Set<T> intersect(Set<T> a, Set<T> b) {
        if(null == a) throw new IllegalArgumentException("Set a cannot be null");
        if(null == b) throw new IllegalArgumentException("Set b cannot be null");
        Set<T> retval = new HashSet<T>(a);
        retval.retainAll(b);
        return Collections.unmodifiableSet(retval);
    }

    public Set<T> union(Set<T> a, Set<T> b) {
        if(null == a) throw new IllegalArgumentException("Set a cannot be null");
        if(null == b) throw new IllegalArgumentException("Set b cannot be null");
        Set<T> retval = new HashSet<T>(a);
        retval.addAll(b);
        return Collections.unmodifiableSet(retval);
    }

    public Set<T> setSubtract(Set<T> a, Set<T> b) {
        if(null == a) throw new IllegalArgumentException("Set a cannot be null");
        if(null == b) throw new IllegalArgumentException("Set b cannot be null");
        Set<T> retval = new HashSet<T>(a);
        retval.removeAll(b);
        return Collections.unmodifiableSet(retval);
    }

    // https://en.wikipedia.org/wiki/Jaccard_index
    public double jaccardSimilarity(Set<T> a, Set<T> b) {
        if(null == a) throw new IllegalArgumentException("Set a cannot be null");
        if(null == b) throw new IllegalArgumentException("Set b cannot be null");
        if(a.isEmpty() && b.isEmpty()) {
            return 1.0;
        }
        final Set<T> intersection = intersect(a, b);
        final int cardinalityIntersection = intersection.size();
        if(0 == cardinalityIntersection) {
            return 0.0;
        }
        final Set<T> union = union(a, b);
        final int cardinalityUnion = union.size();
        return (0.0+cardinalityIntersection)/cardinalityUnion;
    }

    public Set<T> intersectAll(List<List<T>> lol) {
        if(null == lol || lol.isEmpty()) {
            return Collections.emptySet();
        }
        final int size = lol.size();
        if(1 == size) {
            return new HashSet<T>(lol.get(0));
        }
        Set<T> retval = new HashSet<T>();
        Collections.sort(lol, new Comparator<Collection<T>>() {
            @Override
            public int compare(Collection<T> o1, Collection<T> o2) {
                final int o1s = o1.size();
                final int o2s = o2.size();
                if(o1s < o2s) return -1;
                if(o1s > o2s) return 1;
                return 0;
            }
        });
        final List<Set<T>> los = new ArrayList<Set<T>>();
        for(int i=1; i<lol.size(); i++) {
            los.add(new HashSet<T>(lol.get(i)));
        }
        for(final T e : lol.get(0)) {
            boolean inAll = true;
            for(final Set<T> s : los) {
                if(!s.contains(e)) {
                    inAll = false;
                    break;
                }
            }
            if(inAll) {
                retval.add(e);
            }
        }
        return retval;
    }

    public void addAToBUnlessInC(final Set<T> a, final Set<T> b, final Set<T> c) {
        for(final T e : a) {
            if(!c.contains(e)) {
                b.add(e);
            }
        }
    }

    public boolean isSubsetOf(final Set<T> a, final Set<T> b) {
        final int aSize = a.size();
        final int bSize = b.size();
        if(aSize > bSize) return false;
        return b.containsAll(a);
    }

    public Set<T> subset(final Set<T> set, int count) {
        final Set<T> retval = new HashSet<T>(count);

        for(final T t : set) {
            if(count-- <= 0) {
                return retval;
            }
            else {
                retval.add(t);
            }
        }
        return retval;
    }

    public void writeSetToSpreadsheet(MutableInMemorySpreadsheet ss, Set<T> set, final String sheetName) {
        if(null == set) return;
        if(set.isEmpty()) return;
        final T oneElement = set.iterator().next();
        final List<Field> fields = getFields(oneElement);
        final List<String> headers = getHeaders(fields);
        ss.addSheet(headers, sheetName);
        ss.addRow();
        for(final T e : set) {
            for(final Field field : fields) {
                try {
                    final Object valueObject = field.get(e);
                    if(null != valueObject) {
                        ss.addCell(valueObject.toString());
                    }
                    else {
                        ss.addEmptyCell();
                    }
                }
                catch(IllegalAccessException ex) {
                    LOG.warn("cannot add {} to {}", e, this, ex);
                }
            }
            ss.addRow();
        }
    }

    private List<Field> getFields(T e) {
        List<Field> retval = new ArrayList<>();
        final Class<?> clazz = e.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for(final Field field : fields) {
            field.setAccessible(true);
            retval.add(field);
        }
        return retval;
    }

    private List<String> getHeaders(List<Field> fields) {
        List<String> retval = new ArrayList<>();
        for(final Field field : fields) {
            retval.add(field.getName());
        }
        return retval;
    }

    public double jaccardSubsetSimilarity(final Set<T> little, final Set<T> big) {
        final Set<T> intersection = intersect(little, big);
        final double subsetSimilarity = jaccardSimilarity(little, intersection);
        return subsetSimilarity;
    }

}
