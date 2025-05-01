package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public class DoubleCell implements Cell {
    private final Double contents;

    public DoubleCell(Double contents) {
        this.contents = contents;
    }

    @Override
    public String getContentsAsString() {
        return contents.toString();
    }

    @Override
    public void addCellTo(POIBackedMutableInMemorySpreadsheet mims) {
        mims.addCell(contents);
    }
}
