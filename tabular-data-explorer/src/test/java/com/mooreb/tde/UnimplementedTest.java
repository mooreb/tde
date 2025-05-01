package com.mooreb.tde;

import org.testng.annotations.Test;

public class UnimplementedTest {

    @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
    public void unimplemented() {
        throw new UnsupportedOperationException("BUG: Unimplemented"); // BUG: unimplemented
    }
}
