package com.mooreb.tde;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TrivialTests {
    @Test(groups = "unit")
    public void trivialTest1() {
        Assert.assertTrue(true);
    }

    @Test(groups = "unit", expectedExceptions = NullPointerException.class)
    public void negativeTest1() {
        throw new NullPointerException();
    }
}
