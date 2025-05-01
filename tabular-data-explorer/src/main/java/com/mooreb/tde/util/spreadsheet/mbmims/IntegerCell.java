package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public class IntegerCell implements Cell {
    private final Integer contents;

    public IntegerCell(Integer contents) {
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
