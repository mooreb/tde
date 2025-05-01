package com.mooreb.tde.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UniqueCategoryTermCounter<Category, Term> {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Map<Category, Map<Term, Long>> internalMap = new HashMap<>();

    public UniqueCategoryTermCounter() {}

    public UniqueCategoryTermCounter(UniqueCategoryTermCounter<Category,Term> counter) {
        for(final Category category : counter.internalMap.keySet()) {
            final Map<Term, Long> terms = counter.internalMap.get(category);
            final Map<Term, Long> newTerms = new HashMap<Term,Long>(terms);
            this.internalMap.put(category, newTerms);
        }
    }

    public void put(Category category, Term term) {
        if(internalMap.containsKey(category)) {
            Map<Term, Long> termMap = internalMap.get(category);
            if(termMap.containsKey(term)) {
                Long oldCount = termMap.get(term);
                termMap.put(term, 1 + oldCount);
            }
            else {
                termMap.put(term, 1L);
            }
        }
        else {
            Map<Term, Long> newTermMap = new HashMap<Term, Long>();
            newTermMap.put(term, 1L);
            internalMap.put(category, newTermMap);
        }
    }

    /**
     * This code has a bit of a smell to it.
     * It's currently (2019-04-26) only being used where Category and Term are both of type String
     * I could see the argument for the type of "o" to be Term; that would eliminate the second cast.
     * Category must be String
     *
     * It's useful to look at frequency/shared attributes of arbitrary objects, so I'm keeping it
     * but I would like to be able to eliminate the SuppressWarnings annotations and tighten up the types.
     * I just don't see how to do that today.
     *
     * @param o the object to count by category (the names of its internal variables) and term (the values of its internal variables)
     */
    public void putObject(final Object o) {
        final Class<?> clazz = o.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for(final Field field : fields) {
            field.setAccessible(true);
            try {
                final Object valueObject = field.get(o);
                try {
                    final String name = field.getName();
                    @SuppressWarnings("unchecked")
                    final Category category = (Category) name;
                    @SuppressWarnings("unchecked")
                    final Term term = (Term)valueObject;
                    put(category, term);
                }
                catch(ClassCastException e) {
                    LOG.warn("cannot add {} to {}", o, this, e);
                }
            }
            catch(IllegalAccessException e) {
                LOG.warn("cannot add {} to {}", o, this, e);
            }
        }
    }

    public void removeTermForCategory(final Category category, final Term term) {
        final Map<Term,Long> terms = internalMap.get(category);
        if(null != terms) {
            terms.remove(term);
        }
    }

    public void removeNullTerms() {
        for(final Map<Term,Long> terms : internalMap.values()) {
            terms.remove(null);
        }
    }

    public void removeNullCategory() {
        internalMap.remove(null);
    }

    public void removeCategory(final Category category) {
        internalMap.remove(category);
    }


    public Set<Category> getCategories() {
        return internalMap.keySet();
    }

    public Set<Term> getTermsForCategory(Category category) {
        if(internalMap.containsKey(category)) {
            return internalMap.get(category).keySet();
        }
        else {
            return Collections.emptySet();
        }
    }

    public long getFrequency(Category category, Term term) {
        if(internalMap.containsKey(category)) {
            Map<Term, Long> terms = internalMap.get(category);
            if(terms.containsKey(term)) {
                return terms.get(term);
            }
        }
        return 0;
    }

    public long countTermsIncludingDuplicates() {
        long retval = 0;
        for(Map<Term, Long> terms : internalMap.values()) {
            for(Term term : terms.keySet()) {
                long l = terms.get(term);
                retval += l;
            }
        }
        return retval;
    }

    // package scope for tests
    class Bundle implements Comparable<Bundle> {
        private final Category category;
        private final List<InnerBundle> terms;
        private final long termsCount;
        private final long maxTermFreq;

        private Bundle(final Category category, final List<InnerBundle> terms, final long termsCount) {
            this.category = category;
            this.terms = terms;
            this.termsCount = termsCount;
            this.maxTermFreq = computeMaxTermFreq(); // Potential BUG: require that the terms list is passed-in sorted
        }

        // Potential BUG: require that the terms list is passed-in sorted
        private long computeMaxTermFreq() {
            if((null == terms) || terms.isEmpty()) {
                return 0;
            }
            else {
                return terms.get(0).count;
            }
        }

        @Override
        public int compareTo(Bundle o) {
            if(null == o) {
                return -1;
            }
            int cmp1 = Long.compare(o.maxTermFreq, this.maxTermFreq);
            if(0 == cmp1) {
                return Long.compare(o.termsCount, this.termsCount);
            }
            else {
                return cmp1;
            }
        }

        public Category getCategory() {
            return category;
        }

        public List<InnerBundle> getTerms() {
            return terms;
        }

        public long getTermsCount() {
            return termsCount;
        }

        public long getMaxTermFreq() {
            return maxTermFreq;
        }

        @Override
        public String toString() {
            return "Bundle{" +
                    "category=" + category +
                    ", terms=" + terms +
                    ", termsCount=" + termsCount +
                    ", maxTermFreq=" + maxTermFreq +
                    '}';
        }
    }

    // package scope for tests
    class InnerBundle implements Comparable<InnerBundle> {
        private final Term term;
        private final long count;
        private InnerBundle(final Term term, final long count) {
            this.term = term;
            this.count = count;
        }

        @Override
        public int compareTo(InnerBundle o) {
            if(null == o) {
                return -1;
            }
            else {
                return Long.compare(o.count, this.count);
            }
        }

        public Term getTerm() {
            return term;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "InnerBundle{" +
                    "term=" + term +
                    ", count=" + count +
                    '}';
        }
    }

    public class FrequencyTable {
        private final List<Bundle> bundles;
        // Potential BUG: assume bundles is passed in, already sorted
        private FrequencyTable(List<Bundle> bundles) {
            this.bundles = bundles;
        }

        // zero-offset
        public Category getNthMostFrequentCategory(final int n) {
            final Bundle bundle = bundles.get(n);
            return bundle.getCategory();
        }

        // zero-offset
        public Term getNthMostFrequentTermForCategory(final Category category, final int n) {
            for(final Bundle bundle : bundles) {
                if(bundle.getCategory().equals(category)) {
                    final List<InnerBundle> innerBundles = bundle.getTerms();
                    return innerBundles.get(n).getTerm();
                }
            }
            return null;
        }

        public Long getCount(final Category category, final Term term) {
            for(final Bundle bundle : bundles) {
                if(bundle.getCategory().equals(category)) {
                    final List<InnerBundle> innerBundles = bundle.getTerms();
                    for(final InnerBundle innerBundle : innerBundles) {
                        if(innerBundle.getTerm().equals(term)) {
                            return innerBundle.getCount();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "FrequencyTable{" +
                    "bundles=" + bundles +
                    '}';
        }
    }

    public FrequencyTable flatten() {
        final List<Bundle> bundles = rawFlatten();
        final FrequencyTable frequencyTable = new FrequencyTable(bundles);
        return frequencyTable;
    }

    // package scope for tests
    List<Bundle> rawFlatten() {
        List<Bundle> retval = new ArrayList<Bundle>();
        Set<Category> categories = getCategories();
        for(final Category category : categories) {
            final Set<Term> terms = getTermsForCategory(category);
            final List<InnerBundle> termsList = new ArrayList<InnerBundle>();
            long termsCount = 0;
            for(final Term term : terms) {
                final long count = getFrequency(category, term);
                termsCount += count;
                final InnerBundle innerBundle = new InnerBundle(term, count);
                termsList.add(innerBundle);
            }
            Collections.sort(termsList);
            Bundle bundle = new Bundle(category, termsList, termsCount);
            retval.add(bundle);
        }
        Collections.sort(retval);
        return retval;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final List<Bundle> categories = rawFlatten();
        for(final Bundle bundle : categories) {
            final Category category = bundle.category;
            final long termsCount = bundle.termsCount;
             final List<InnerBundle> terms = bundle.terms;
            final int numUniqueTerms = terms.size();
            sb.append(category);
            sb.append("[");
            sb.append(termsCount);
            sb.append(", ");
            sb.append(numUniqueTerms);
            sb.append("]");
            sb.append(": ");
            final Iterator<InnerBundle> iterator = terms.iterator();
            while(iterator.hasNext()) {
                InnerBundle innerBundle = iterator.next();
                final long count = innerBundle.count;
                final double percent = ((0.0 + count)/termsCount);
                sb.append(innerBundle.term);
                sb.append("[");
                sb.append(count);
                sb.append("; ");
                sb.append(StringUtils.myPercent(percent));
                sb.append("]");
                if(iterator.hasNext()) {
                    sb.append(", ");
                }
                else {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
