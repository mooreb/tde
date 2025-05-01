package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public interface Cell {
    String getContentsAsString();
    void addCellTo(final POIBackedMutableInMemorySpreadsheet mims);
}
