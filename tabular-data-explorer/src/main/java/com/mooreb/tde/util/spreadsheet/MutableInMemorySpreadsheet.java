package com.mooreb.tde.util.spreadsheet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface MutableInMemorySpreadsheet {
    void addSheet(List<String> headers, String sheetName);

    void addRow();

    void addEmptyCell();

    void addCell(String contents);

    void addCell(Integer contents);

    void addCell(Double contents);

    void addPercentageCell(Double contents);

    void addCell(Date contents);

    void protect();

    /**
     *
     * @param columnIndex column A is offset 0
     * @param width multiply what excel says the width is by 256 to get the proper input
     */
    void setColumnWidth(int columnIndex, int width);

    File write() throws IOException;
}
