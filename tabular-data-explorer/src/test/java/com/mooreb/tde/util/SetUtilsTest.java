package com.mooreb.tde.util;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SetUtilsTest {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SetUtils<String> suString = new SetUtils<String>();

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testIntersectNullA() {
        final Set<String> a = null;
        final Set<String> b = new HashSet<String>();
        final Set<String> intersection = suString.intersect(a,b);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testIntersectNullB() {
        final Set<String> a = new HashSet<String>();
        final Set<String> b = null;
        final Set<String> intersection = suString.intersect(a,b);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testUnionNullA() {
        final Set<String> a = null;
        final Set<String> b = new HashSet<String>();
        final Set<String> union = suString.union(a,b);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testUnionNullB() {
        final Set<String> a = new HashSet<String>();
        final Set<String> b = null;
        final Set<String> intersection = suString.union(a,b);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testSetSubtractNullA() {
        final Set<String> a = null;
        final Set<String> b = new HashSet<String>();
        final Set<String> setSubtractResult = suString.setSubtract(a,b);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testSetSubtractNullB() {
        final Set<String> a = new HashSet<String>();
        final Set<String> b = null;
        final Set<String> setSubtractResult = suString.setSubtract(a,b);
    }

    @Test(groups = "unit")
    public void testIntersection() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("b");
        desiredResult.add("c");
        Assert.assertEquals(suString.intersect(a, b), desiredResult, "expect intersection of b,c");
    }

    @Test(groups = "unit")
    public void testUnion() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("a");
        desiredResult.add("b");
        desiredResult.add("c");
        desiredResult.add("d");
        Assert.assertEquals(suString.union(a, b), desiredResult, "expect union of a,b,c,d");
    }


    @Test(groups = "unit")
    public void testSetSubtract() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("a");
        Assert.assertEquals(suString.setSubtract(a, b), desiredResult, "expect setSubtract of a");
    }

    @Test(groups = "unit")
    public void testIntersectionWithField() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("b");
        desiredResult.add("c");
        Assert.assertEquals(SetUtils.STRING.intersect(a, b), desiredResult, "expect intersection of b,c");
    }

    @Test(groups = "unit")
    public void testUnionWithField() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("a");
        desiredResult.add("b");
        desiredResult.add("c");
        desiredResult.add("d");
        Assert.assertEquals(SetUtils.STRING.union(a, b), desiredResult, "expect union of a,b,c,d");
    }

    @Test(groups = "unit")
    public void testSetSubtractWithField() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        desiredResult.add("a");
        Assert.assertEquals(SetUtils.STRING.setSubtract(a, b), desiredResult, "expect setSubtract of a");
    }

    @Test(groups = "unit")
    public void testJaccardSimilarityWithField() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b c d
        final double desiredResult=0.5;
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        b.add("c");
        b.add("d");
        Assert.assertEquals(SetUtils.STRING.jaccardSimilarity(a, b), desiredResult, "expect 0.5");
    }

    @Test(groups = "unit")
    public void testIntersectAll() {
        final List<String> l1 = new ArrayList<String>(); // a b c
        final List<String> l2 = new ArrayList<String>(); //   b c d
        final List<String> l3 = new ArrayList<String>(); //     c d e
        l1.add("a");
        l1.add("b");
        l1.add("c");
        l2.add("b");
        l2.add("c");
        l2.add("d");
        l3.add("c");
        l3.add("d");
        l3.add("e");
        final List<List<String>> lol = new ArrayList<List<String>>();
        lol.add(l1);
        lol.add(l2);
        lol.add(l3);
        final Set<String> intersection = SetUtils.STRING.intersectAll(lol);
        Assert.assertNotNull(intersection);
        Assert.assertEquals(intersection.size(), 1);
        Assert.assertTrue(intersection.contains("c"));
    }

    @Test(groups = "unit")
    public void addAToBUnlessInCTest() {
        final Set<String> a = new HashSet<String>(); // a b c
        final Set<String> b = new HashSet<String>(); // b
        final Set<String> c = new HashSet<String>(); // c
        final Set<String> desiredResult = new HashSet<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        b.add("b");
        c.add("c");
        desiredResult.add("a");
        desiredResult.add("b");
        SetUtils.STRING.addAToBUnlessInC(a, b, c);
        Assert.assertEquals(b, desiredResult);
    }

    @Test(groups = "unit")
    public void testShortSubset() {
        Set<String> foo = new HashSet<>();
        foo.add("1");
        foo.add("1");
        foo.add("1");
        foo.add("1");
        foo.add("1");
        foo.add("1");
        foo.add("1");
        Set<String> bar = SetUtils.STRING.subset(foo, 3);
        Assert.assertEquals(bar.size(), 1);
    }

    @Test(groups = "unit")
    public void testSubset() {
        Set<String> foo = new HashSet<>();
        foo.add("1");
        foo.add("2");
        foo.add("3");
        foo.add("4");
        foo.add("5");
        foo.add("6");
        foo.add("7");
        Set<String> bar = SetUtils.STRING.subset(foo, 3);
        Assert.assertEquals(bar.size(), 3);
    }

}
