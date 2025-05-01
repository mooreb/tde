package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public class EmptyCell implements Cell {

    public EmptyCell() {
    }

    @Override
    public String getContentsAsString() {
        return "";
    }

    @Override
    public void addCellTo(POIBackedMutableInMemorySpreadsheet mims) {
        mims.addEmptyCell();
    }
}
