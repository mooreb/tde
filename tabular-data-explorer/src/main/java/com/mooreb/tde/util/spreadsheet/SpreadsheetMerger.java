package com.mooreb.tde.util.spreadsheet;

import java.io.IOException;
import java.util.List;

public class SpreadsheetMerger {
    private final List<InMemorySpreadsheet> spreadsheets;

    public SpreadsheetMerger(final List<InMemorySpreadsheet> spreadsheets) {
        if(null == spreadsheets) {
            throw new IllegalArgumentException("cannot have null spreadsheets");
        }
        if(spreadsheets.isEmpty()) {
            throw new IllegalArgumentException("cannot merge an empty list of spreadsheets");
        }
        if(1 == spreadsheets.size()) {
            throw new IllegalArgumentException("don't try to waste time by merging a single spreadsheet");
        }
        this.spreadsheets = spreadsheets;
    }

    public void merge(final String outputFileName) throws IOException {
        final MutableInMemorySpreadsheet out = new POIBackedMutableInMemorySpreadsheet(outputFileName);
        final List<String> headers = spreadsheets.get(0).getHeaders();
        final String sheetName = spreadsheets.get(0).getSheetNames().get(0);
        out.addSheet(headers, sheetName);
        for(final InMemorySpreadsheet ss : spreadsheets) {
            for(final Row row : ss.getListOfRows()) {
                out.addRow();
                for(final String h : headers) {
                    final String contents = row.getContentsUpper(h);
                    out.addCell(contents);
                }
            }
        }
        out.write();
    }
}
