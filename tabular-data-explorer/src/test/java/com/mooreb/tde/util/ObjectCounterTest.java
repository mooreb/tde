package com.mooreb.tde.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ObjectCounterTest {

    @Test(groups = "unit")
    public void ensureSorted() {
        final String abc = "abc";
        final String def = "def";
        final String ghi = "ghi";
        final String jkl = "jkl";
        final ObjectCounter<String> objectCounter = new ObjectCounter<>();
        for(int i=0; i<100; i++) {
            objectCounter.add(abc);
        }
        for(int i=0; i<90; i++) {
            objectCounter.add(def);
        }
        for(int i=0; i<80; i++) {
            objectCounter.add(ghi);
        }
        for(int i=0; i<70; i++) {
            objectCounter.add(jkl);
        }
        final List<ObjectCounter<String>.Entry> strings = objectCounter.sortByCount();
        Assert.assertEquals(strings.get(0).getValue(), abc);
        Assert.assertEquals(strings.get(0).getCount(), 100);
        Assert.assertEquals(strings.get(1).getValue(), def);
        Assert.assertEquals(strings.get(1).getCount(), 90);
        Assert.assertEquals(strings.get(2).getValue(), ghi);
        Assert.assertEquals(strings.get(2).getCount(), 80);
        Assert.assertEquals(strings.get(3).getValue(), jkl);
        Assert.assertEquals(strings.get(3).getCount(), 70);
        Assert.assertEquals(objectCounter.getMostFrequentValue(), abc);
    }
}
