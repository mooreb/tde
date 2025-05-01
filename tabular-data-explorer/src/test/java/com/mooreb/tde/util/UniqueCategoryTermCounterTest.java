package com.mooreb.tde.util;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


public class UniqueCategoryTermCounterTest {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test(groups = "unit")
    public void one() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        counter.put("k1", "k1v1");
        counter.put("k2", "k2v2");
        counter.put("k3", "k3v3");
        counter.put("k1", "k1v2");
        counter.put("k1", "k1v3");
        counter.put("k1", "k1v1");
        Assert.assertEquals(counter.getCategories().size(), 3);
        Assert.assertEquals(counter.getTermsForCategory("k1").size(), 3);
        Assert.assertEquals(counter.getTermsForCategory("k2").size(), 1);
        Assert.assertEquals(counter.getTermsForCategory("k3").size(), 1);
        Assert.assertEquals(counter.countTermsIncludingDuplicates(), 6);
        Assert.assertEquals(counter.getFrequency("k1", "k1v1"), 2);
        Assert.assertEquals(counter.getFrequency("k3", "k1v1"), 0);
        Assert.assertEquals(counter.getFrequency("k3", "k3v3"), 1);
    }

    @Test(groups = "unit")
    public void putNullCategory() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        counter.put(null, "nonnull");
        final Set<String> categories = counter.getCategories();
        Assert.assertEquals(categories.size(), 1, "expected the null put to be its own category");
        LOG.info(counter.toString());
    }

    @Test(groups = "unit")
    public void putNullTerm() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        counter.put("non-null", null);
        final Set<String> categories = counter.getCategories();
        Assert.assertEquals(categories.size(), 1, "expected the null put to be its own category");
        final Set<String> terms = counter.getTermsForCategory("non-null");
        Assert.assertEquals(terms.size(), 1, "expected the null put to be a viable term");
        LOG.info(counter.toString());
    }

    @Test(groups = "unit")
    public void putBothNull() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        counter.put(null, null);
        final Set<String> categories = counter.getCategories();
        Assert.assertEquals(categories.size(), 1, "expected the null put to be its own category");
        final Set<String> terms = counter.getTermsForCategory(null);
        Assert.assertEquals(terms.size(), 1, "expected the null put to be a viable term");
        LOG.info(counter.toString());
    }

    @Test(groups = "unit")
    public void testSortingWithRawFlatten() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        for(int i=0; i<50; i++) {
            counter.put("c1", "t1");
        }
        for(int i=0; i<26; i++) {
            counter.put("c2", "t2");
        }
        for(int i=0; i<25; i++) {
            counter.put("c2", "t3");
        }
        counter.put("c3", "t4");
        counter.put("c3", "t5");
        counter.put("c3", "t6");
        final List<UniqueCategoryTermCounter<String,String>.Bundle> bundles = counter.rawFlatten();
        Assert.assertEquals(bundles.size(), 3);
        UniqueCategoryTermCounter<String,String>.Bundle b1 = bundles.get(0);
        Assert.assertNotNull(b1);
        UniqueCategoryTermCounter<String,String>.Bundle b2 = bundles.get(1);
        Assert.assertNotNull(b2);
        UniqueCategoryTermCounter<String,String>.Bundle b3 = bundles.get(2);
        Assert.assertNotNull(b3);
        Assert.assertEquals(b1.getCategory(), "c1");
        Assert.assertEquals(b2.getCategory(), "c2");
        Assert.assertEquals(b3.getCategory(), "c3");
        List<UniqueCategoryTermCounter<String,String>.InnerBundle> b1Terms = b1.getTerms();
        Assert.assertNotNull(b1Terms);
        List<UniqueCategoryTermCounter<String,String>.InnerBundle> b2Terms = b2.getTerms();
        Assert.assertNotNull(b2Terms);
        List<UniqueCategoryTermCounter<String,String>.InnerBundle> b3Terms = b3.getTerms();
        Assert.assertNotNull(b3Terms);
        Assert.assertEquals(b1Terms.size(), 1);
        Assert.assertEquals(b2Terms.size(), 2);
        Assert.assertEquals(b3Terms.size(), 3);
        Assert.assertEquals(b1Terms.get(0).getTerm(), "t1");
        Assert.assertEquals(b2Terms.get(0).getTerm(), "t2");
        Assert.assertEquals(b2Terms.get(1).getTerm(), "t3");

    }

    @Test(groups = "unit")
    public void testSortingWithFlatten() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        for(int i=0; i<50; i++) {
            counter.put("c1", "t1");
        }
        for(int i=0; i<26; i++) {
            counter.put("c2", "t2");
        }
        for(int i=0; i<25; i++) {
            counter.put("c2", "t3");
        }
        counter.put("c3", "t4");
        counter.put("c3", "t5");
        counter.put("c3", "t6");
        final UniqueCategoryTermCounter<String,String>.FrequencyTable frequencyTable = counter.flatten();
        Assert.assertEquals(frequencyTable.getNthMostFrequentCategory(0), "c1");
        Assert.assertEquals(frequencyTable.getNthMostFrequentCategory(1), "c2");
        Assert.assertEquals(frequencyTable.getNthMostFrequentCategory(2), "c3");
        Assert.assertEquals(frequencyTable.getNthMostFrequentTermForCategory("c1", 0), "t1");
        Assert.assertEquals(frequencyTable.getNthMostFrequentTermForCategory("c2", 0), "t2");
        Assert.assertEquals(frequencyTable.getNthMostFrequentTermForCategory("c2", 1), "t3");
    }

    @Test(groups = "unit")
    public void testRemoveNullTerms() {
        UniqueCategoryTermCounter<String,String> counter = new UniqueCategoryTermCounter<>();
        counter.put("c1", null);
        counter.put("c1", null);
        counter.put("c1", "t1");
        final UniqueCategoryTermCounter<String,String>.FrequencyTable frequencyTable1 = counter.flatten();
        Assert.assertNull(frequencyTable1.getNthMostFrequentTermForCategory("c1", 0));
        counter.removeNullTerms();
        final UniqueCategoryTermCounter<String,String>.FrequencyTable frequencyTable2 = counter.flatten();
        Assert.assertEquals(frequencyTable2.getNthMostFrequentTermForCategory("c1", 0), "t1");
    }

    private class Foo {
        private final String a1;
        private final String a2;
        private final String a3;

        public Foo(String a1, String a2, String a3) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
        }
        public Foo() {
            this.a1 = this.a2 = this.a3 = null;
        }
    }

    @Test(groups = "unit")
    public void putObjectTest() {
        UniqueCategoryTermCounter<String, String> counter = new UniqueCategoryTermCounter<>();
        final Foo f1 = new Foo();
        final Foo f2 = new Foo("one", "two", "three");
        counter.putObject(f1);
        counter.putObject(f2);
        counter.putObject(f2);
        final UniqueCategoryTermCounter<String,String>.FrequencyTable frequencyTable = counter.flatten();
        final String actualA1 = frequencyTable.getNthMostFrequentTermForCategory("a1", 0);
        Assert.assertEquals(actualA1, "one");
        final String actualA2 = frequencyTable.getNthMostFrequentTermForCategory("a2", 0);
        Assert.assertEquals(actualA2, "two");
        final String actualA3 = frequencyTable.getNthMostFrequentTermForCategory("a3", 0);
        Assert.assertEquals(actualA3, "three");
        // System.out.println(counter.toString());

    }

}
