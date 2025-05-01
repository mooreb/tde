package com.mooreb.tde.util.spreadsheet;

import java.util.List;

public interface InMemorySpreadsheet {
    List<Row> getListOfRows();
    Row getRow(int i);
    int getNumRows();
    List<String> getHeaders();
    List<String> getSheetNames();
}
