package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public class StringCell implements Cell {
    private final String contents;

    public StringCell(String contents) {
        this.contents = contents;
    }

    @Override
    public String getContentsAsString() {
        return contents;
    }

    @Override
    public void addCellTo(POIBackedMutableInMemorySpreadsheet mims) {
        mims.addCell(contents);
    }
}
