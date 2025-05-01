package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;

public class MutableInMemorySpreadsheetTest {
    @Test(groups = "unit")
    public void cellLimitTest32767() {
        final String fname = StringUtils.myInstant() + ".xlsx";
        final POIBackedMutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("foo");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        final StringBuffer sb = new StringBuffer();
        for(int i=0; i<32767; i++) {
            sb.append("x");
        }
        mutableInMemorySpreadsheet.addRow();
        mutableInMemorySpreadsheet.addStringCellRaw(sb.toString());
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void cellLimitTest32768Raw() {
        final String fname = StringUtils.myInstant() + ".xlsx";
        final POIBackedMutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("foo");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        final StringBuffer sb = new StringBuffer();
        for(int i=0; i<32768; i++) {
            sb.append("x");
        }
        mutableInMemorySpreadsheet.addRow();
        mutableInMemorySpreadsheet.addStringCellRaw(sb.toString());
    }

    @Test(groups = "unit")
    public void cellLimitTest32768Chunked() {
        final String fname = StringUtils.myInstant() + ".xlsx";
        final MutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("foo");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        final StringBuffer sb = new StringBuffer();
        for(int i=0; i<32768; i++) {
            sb.append("x");
        }
        mutableInMemorySpreadsheet.addRow();
        mutableInMemorySpreadsheet.addCell(sb.toString());
    }

    @Test(groups = "integration")
    public void overflowTest() {
        final String fname = StringUtils.myInstant() + ".xlsx";
        final MutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("foo");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        mutableInMemorySpreadsheet.addRow();
        mutableInMemorySpreadsheet.addCell("foo1"); // normal, necessary
        mutableInMemorySpreadsheet.addCell("overflowcell1"); // overflow
        mutableInMemorySpreadsheet.addCell("overflowcell2"); // overflow
    }

    @Test(groups = "integration")
    public void underflowTest() {
        final String fname = StringUtils.myInstant() + ".xlsx";
        final MutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("foo");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        mutableInMemorySpreadsheet.addRow(); // normal, necessary
        mutableInMemorySpreadsheet.addRow(); // underflow
    }

    @Test(groups = "integration")
    public void percentTest() throws IOException {
        final String fname = StringUtils.generateUniqueExcelName("percent-test");
        final MutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("percent");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        final Random random = new Random();
        double p = 0.0;
        while(p < 100) {
            mutableInMemorySpreadsheet.addRow();
            mutableInMemorySpreadsheet.addPercentageCell(p);
            p += random.nextDouble();
        }
        mutableInMemorySpreadsheet.write();
    }

    @Test(groups = "integration")
    public void miscValuesTest() throws IOException {
        final String fname = StringUtils.generateUniqueExcelName("misc-values-test") + ".xlsx";
        final MutableInMemorySpreadsheet mutableInMemorySpreadsheet = new POIBackedMutableInMemorySpreadsheet(fname);
        final List<String> headers = new ArrayList<String>();
        headers.add("integer null");
        headers.add("string null");
        headers.add("empty string");
        headers.add("one blank");
        headers.add("two blanks");
        headers.add("x");
        final String sheetName = "sheetName";
        mutableInMemorySpreadsheet.addSheet(headers, sheetName);
        mutableInMemorySpreadsheet.addRow();
        final Integer integerNull = null;
        mutableInMemorySpreadsheet.addCell(integerNull);
        final String stringNull = null;
        mutableInMemorySpreadsheet.addCell(stringNull);
        mutableInMemorySpreadsheet.addCell("");
        mutableInMemorySpreadsheet.addCell(" ");
        mutableInMemorySpreadsheet.addCell("  ");
        mutableInMemorySpreadsheet.addCell("x");
        mutableInMemorySpreadsheet.write();
    }

}
