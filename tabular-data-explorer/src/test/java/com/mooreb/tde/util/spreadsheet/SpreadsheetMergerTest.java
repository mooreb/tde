package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public class SpreadsheetMergerTest {

    @Test(groups = "integration")
    public void testMerge() throws InMemorySpreadsheetException, IOException {
        final InputStream is1 = this.getClass().getClassLoader().getResourceAsStream("m1.xlsx");
        final InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("m2.xlsx");
        final InMemorySpreadsheet m1 = new InMemorySpreadsheetFromPOI(is1);
        final InMemorySpreadsheet m2 = new InMemorySpreadsheetFromPOI(is2);
        final List<InMemorySpreadsheet> spreadsheets = new ArrayList<InMemorySpreadsheet>();
        spreadsheets.add(m1);
        spreadsheets.add(m2);
        final SpreadsheetMerger spreadsheetMerger = new SpreadsheetMerger(spreadsheets);
        final String outputFileName = StringUtils.generateUniqueExcelName("m3");
        spreadsheetMerger.merge(outputFileName);
    }
}
