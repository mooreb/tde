package com.mooreb.tde.util.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemorySpreadsheetFromListOfInMemorySpreadsheets implements InMemorySpreadsheet {
    private final int numRows;
    private final List<Row> rows;
    private final List<String> headers;
    private final List<String> sheetNames;

    public InMemorySpreadsheetFromListOfInMemorySpreadsheets(final List<InMemorySpreadsheet> spreadsheets) {
        int numRows = 0;
        final List<Row> rows = new ArrayList<>();
        final List<String> sheetNames = new ArrayList<>();
        for(final InMemorySpreadsheet ss : spreadsheets) {
            numRows += ss.getNumRows();
            rows.addAll(ss.getListOfRows());
            sheetNames.addAll(ss.getSheetNames());
        }
        this.numRows = numRows;
        this.rows = Collections.unmodifiableList(rows);
        final List<String> headers = spreadsheets.get(0).getHeaders(); // Potential BUG: take headers from the first ss without checking
        this.headers = Collections.unmodifiableList(headers);
        this.sheetNames = Collections.unmodifiableList(sheetNames);
    }

    @Override
    public List<Row> getListOfRows() {
        return rows;
    }

    @Override
    public Row getRow(int i) {
        return rows.get(i);
    }

    @Override
    public int getNumRows() {
        return numRows;
    }

    @Override
    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public List<String> getSheetNames() {
        return sheetNames;
    }
}
