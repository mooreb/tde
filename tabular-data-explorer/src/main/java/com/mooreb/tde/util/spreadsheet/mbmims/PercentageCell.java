package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.StringUtils;
import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;

public class PercentageCell implements Cell {
    private final Double contents;

    public PercentageCell(final Double contents) {
        this.contents = contents;
    }

    @Override
    public String getContentsAsString() {
        return StringUtils.myPercent(contents);
    }

    @Override
    public void addCellTo(POIBackedMutableInMemorySpreadsheet mims) {
        mims.addPercentageCell(contents);
    }
}
