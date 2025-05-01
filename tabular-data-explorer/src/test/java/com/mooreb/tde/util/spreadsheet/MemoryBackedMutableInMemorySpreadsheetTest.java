package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.spreadsheet.mbmims.MemoryBackedMutableInMemorySpreadsheet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;

public class MemoryBackedMutableInMemorySpreadsheetTest {

    @Test(groups="integration")
    public void writeEmptyTildeText() throws IOException {
        final MemoryBackedMutableInMemorySpreadsheet mims = new MemoryBackedMutableInMemorySpreadsheet("foo");
        final List<String> headers = getHeaders();
        mims.addSheet(headers, "bar");
        mims.forceTildeText();
        mims.write();
    }

    @Test(groups="integration")
    public void writeEmptyRowTildeText() throws IOException {
        final MemoryBackedMutableInMemorySpreadsheet mims = new MemoryBackedMutableInMemorySpreadsheet("foo");
        final List<String> headers = getHeaders();
        mims.addSheet(headers, "bar");
        mims.addRow();
        mims.forceTildeText();
        mims.write();
    }

    @Test(groups="integration")
    public void writeEmptyRowAndOneElementRowTildeText() throws IOException {
        final MemoryBackedMutableInMemorySpreadsheet mims = new MemoryBackedMutableInMemorySpreadsheet("foo");
        final List<String> headers = getHeaders();
        mims.addSheet(headers, "bar");
        mims.addRow();
        mims.addRow();
        mims.addCell(1);
        mims.forceTildeText();
        mims.write();
    }

    private List<String> getHeaders() {
        final List<String> retval = new ArrayList<>();
        retval.add("h1");
        retval.add("h2");
        retval.add("h3");
        return Collections.unmodifiableList(retval);
    }
}
