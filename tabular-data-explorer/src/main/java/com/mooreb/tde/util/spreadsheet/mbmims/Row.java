package com.mooreb.tde.util.spreadsheet.mbmims;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Row {
    private final List<Cell> cells = new ArrayList<>();

    public Row() {}

    public void addCell(final Cell cell) {
        cells.add(cell);
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }
}
