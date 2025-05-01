package com.mooreb.tde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

public class BooleanTest {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final static Boolean booleanNull = null;
    private final static Boolean booleanTrue = true;
    private final static Boolean booleanFalse = false;

    @Test(groups = "unit", expectedExceptions = NullPointerException.class)
    public void nullTest() {
        Assert.assertFalse(booleanNull);
    }

    @Test(groups = "unit")
    public void falseTest() {
        Assert.assertFalse(booleanFalse);
    }

    @Test(groups = "unit")
    public void trueTest() {
        Assert.assertTrue(booleanTrue);
    }

    @Test(groups = "unit", expectedExceptions = NullPointerException.class)
    public void testNullBooleanInIf() {
        if(booleanNull) {
            LOG.info("boolean null evaluated true");
        }
        else {
            LOG.info("boolean null evaluated false");
        }
    }

    @Test(groups = "unit")
    public void testAgainstNull() {
        if(null == booleanNull) {
            LOG.info("boolean null evaluated null");
        }
    }

    @Test(groups = "unit")
    public void testAgainstFalse() {
        Assert.assertTrue(false == booleanFalse);
    }
}
