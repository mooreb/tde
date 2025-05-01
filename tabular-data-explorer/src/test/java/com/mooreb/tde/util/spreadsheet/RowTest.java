package com.mooreb.tde.util.spreadsheet;

import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RowTest {

    @Test(groups = "unit")
    public void nullTest() {
        Row row = new Row(null);
        String contents = row.getContentsUpper(null);
        Assert.assertNull(contents);
    }

    @Test(groups = "unit")
    public void nullTest2() {
        Map<String,String> m = new HashMap<String,String>();
        m.put(null, "contents");
        Row row = new Row(m);
        String contents = row.getContentsUpper(null);
        Assert.assertNull(contents, "expect contents to be null");
    }

}
