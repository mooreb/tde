package com.mooreb.tde.util;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MultiMapTest {

    @Test(groups = "unit")
    public void one() {
        MultiMap<String,String> mm = new MutableMultiMap<>();
        mm.put("k", "v1");
        mm.put("k", "v2");
        List<String> vals = mm.get("k");
        Assert.assertNotNull(vals);
        Assert.assertEquals(vals.size(), 2, "expect two things in the list");
    }
}
