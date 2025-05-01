package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sheet {
    private final String sheetName;
    private final List<String> headers;
    private final List<Row> rows = new ArrayList<>();

    public Sheet(final List<String> headers, final String sheetName) {
        if(null == headers) {
            throw new IllegalArgumentException("cannot have null headers");
        }
        if(null == sheetName) {
            throw new IllegalArgumentException("cannot have null sheet name");
        }
        if(headers.isEmpty()) {
            throw new IllegalArgumentException("cannot have empty headers");
        }
        if(StringUtils.isEmpty(sheetName)) {
            throw new IllegalArgumentException("cannot have empty sheet name");
        }
        final Set<String> headerSet = new HashSet<>(headers);
        if(headerSet.size() != headers.size()) {
            throw new IllegalArgumentException("cannot have duplicate headers");
        }

        this.headers = headers;
        this.sheetName = sheetName;
    }

    public void addRow() {
        final Row row = new Row();
        rows.add(row);
    }

    public void addCell(final Cell cell) {
        if(rows.isEmpty()) throw new IllegalStateException("need to add a row before adding a cell");
        final int currentRowIndex = rows.size() - 1;
        final Row currentRow = rows.get(currentRowIndex);
        currentRow.addCell(cell);
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public int getNumRows() {
        return rows.size();
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }

}
