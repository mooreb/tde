package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.MultiMap;
import com.mooreb.tde.util.MutableMultiMap;
import com.mooreb.tde.util.StringUtils;
import com.mooreb.tde.util.spreadsheet.MutableInMemorySpreadsheet;
import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryBackedMutableInMemorySpreadsheet implements MutableInMemorySpreadsheet {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String fileNamePrefix;
    private final List<Sheet> sheets = new ArrayList<>();
    private boolean shouldProtect = false;
    private boolean forceTildeText = false;

    public MemoryBackedMutableInMemorySpreadsheet(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    @Override
    public void addSheet(List<String> headers, String sheetName) {
        final Sheet sheet = new Sheet(headers, sheetName);
        sheets.add(sheet);
    }

    @Override
    public void addRow() {
        if(sheets.isEmpty()) throw new IllegalStateException("need to add a sheet before adding a row");
        final int currentSheetIndex = sheets.size() - 1;
        final Sheet currentSheet = sheets.get(currentSheetIndex);
        currentSheet.addRow();
    }

    private void addCell(final Cell cell) {
        if(sheets.isEmpty()) throw new IllegalStateException("need to add a sheet before adding a cell");
        final int currentSheetIndex = sheets.size() - 1;
        final Sheet currentSheet = sheets.get(currentSheetIndex);
        currentSheet.addCell(cell);
    }

    @Override
    public void addEmptyCell() {
        addCell(new EmptyCell());
    }

    @Override
    public void addCell(String contents) {
        addCell(new StringCell(contents));
    }

    @Override
    public void addCell(Integer contents) {
       addCell(new IntegerCell(contents));
    }

    @Override
    public void addCell(Double contents) {
        addCell(new DoubleCell(contents));
    }

    @Override
    public void addPercentageCell(Double contents) {
        addCell(new PercentageCell(contents));
    }

    @Override
    public void addCell(Date contents) {
       addCell(new DateCell(contents));
    }

    @Override
    public void protect() {
        shouldProtect = true;
    }

    public void forceTildeText() {
        forceTildeText = true;
    }

    private class ColumnIndexAndWidth {
        private final int columnIndex;
        private final int width;

        public ColumnIndexAndWidth(int columnIndex, int width) {
            this.columnIndex = columnIndex;
            this.width = width;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public int getWidth() {
            return width;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ColumnIndexAndWidth that = (ColumnIndexAndWidth) o;

            if (columnIndex != that.columnIndex) return false;
            return width == that.width;
        }

        @Override
        public int hashCode() {
            int result = columnIndex;
            result = 31 * result + width;
            return result;
        }
    }

    private final MultiMap<Integer, ColumnIndexAndWidth> widthAdjustments = new MutableMultiMap<>();

    @Override
    public void setColumnWidth(int columnIndex, int width) {
        widthAdjustments.put(sheets.size(), new ColumnIndexAndWidth(columnIndex, width));
    }

    private static final int EXCEL_ROW_LIMIT=1048576;
    private boolean fitsForPOI() {
        for(final Sheet sheet : sheets) {
            final int numRows = sheet.getNumRows();
            if(numRows > (EXCEL_ROW_LIMIT - 1)) { // minus 1 for the header
                return false;
            }
        }
        return true;
    }

    @Override
    public File write() throws IOException {
        final boolean fitsForPOI = fitsForPOI();
        if(forceTildeText || !fitsForPOI) {
            return writeAsTildeText();
        }
        else {
            return writeAsPOI();
        }
    }

    private File writeAsPOI() throws IOException {
        final String fileName = StringUtils.generateUniqueExcelName(fileNamePrefix);
        final POIBackedMutableInMemorySpreadsheet mims = new POIBackedMutableInMemorySpreadsheet(fileName);
        int sheetNumber = 0;
        for(final Sheet sheet : sheets) {
            sheetNumber++;
            mims.addSheet(sheet.getHeaders(), sheet.getSheetName());
            writeRows(mims, sheet);
            adjustWidth(mims, sheetNumber);
        }
        if(shouldProtect) {
            mims.protect();
        }
        return mims.write();
    }

    private void writeRows(POIBackedMutableInMemorySpreadsheet mims, Sheet sheet) {
        final List<Row> rows = sheet.getRows();
        for(final Row row : rows) {
            mims.addRow();
            final List<Cell> cells = row.getCells();
            for(final Cell cell : cells) {
                cell.addCellTo(mims);
            }
        }
    }

    private void adjustWidth(POIBackedMutableInMemorySpreadsheet mims, int sheetNumber) {
        final List<ColumnIndexAndWidth> theseAdjustments = widthAdjustments.get(sheetNumber);
        if(null != theseAdjustments) {
            for (final ColumnIndexAndWidth columnIndexAndWidth : theseAdjustments) {
                mims.setColumnWidth(columnIndexAndWidth.getColumnIndex(), columnIndexAndWidth.getWidth());
            }
        }
    }

    private File writeAsTildeText() throws IOException {
        File retval = null;
        final List<File> files = writeSheetsAsTildeText();
        if(files.size() >= 1) {
            // Potential BUG: return only the first sheet
            retval = files.get(0);
        }
        return retval;
    }

    private List<File> writeSheetsAsTildeText() throws IOException {
        final List<File> retval = new ArrayList<>();
        for(final Sheet sheet : sheets) {
            final File file = writeSheetAsTildeText(sheet);
            retval.add(file);
        }
        return Collections.unmodifiableList(retval);
    }

    private File writeSheetAsTildeText(final Sheet sheet) throws IOException {
        final String sheetNamePrefix = fileNamePrefix + "-" + sheet.getSheetName();
        final String fileName = StringUtils.generateUniqueTildeTextName(sheetNamePrefix);
        final File retval = new File(fileName);
        final FileWriter fileWriter = new FileWriter(retval);
        final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        addHeaders(bufferedWriter, sheet);
        writeRows(bufferedWriter, sheet);
        cleanUp(fileWriter, bufferedWriter);
        return retval;
    }

    private void cleanUp(FileWriter fileWriter, BufferedWriter bufferedWriter) {
        try {
            bufferedWriter.flush();
        }
        catch(IOException e) {
            LOG.warn("caught exception trying to clean up", e);
        }
        try {
            bufferedWriter.close();
        }
        catch(IOException e) {
            LOG.warn("caught exception trying to clean up", e);
        }
        try {
            fileWriter.flush();
        }
        catch(IOException e) {
            LOG.warn("caught exception trying to clean up", e);
        }
        try {
            fileWriter.close();
        }
        catch(IOException e) {
            LOG.warn("caught exception trying to clean up", e);
        }
    }

    private void addHeaders(final BufferedWriter bufferedWriter, final Sheet sheet) throws IOException {
        final List<String> headers = sheet.getHeaders();
        final int numHeaders = headers.size();
        for(int i=0; i < (numHeaders - 1); i++) {
            final String header = headers.get(i);
            bufferedWriter.write(header);
            bufferedWriter.write("~");
        }
        bufferedWriter.write(headers.get(numHeaders-1));
        bufferedWriter.newLine();
    }

    private void writeRows(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        final List<Row> rows = sheet.getRows();
        for(final Row row : rows) {
            writeRow(bufferedWriter, row);
        }
    }

    private void writeRow(BufferedWriter bufferedWriter, Row row) throws IOException {
        final List<Cell> cells = row.getCells();
        final int numCells = cells.size();
        for(int i=0; i<(numCells-1); i++) {
            final Cell cell = cells.get(i);
            final String string = cell.getContentsAsString();
            if(null != string) bufferedWriter.write(string);
            bufferedWriter.write("~");
        }
        if(numCells >= 1) {
            final Cell cell = cells.get(numCells - 1);
            final String string = cell.getContentsAsString();
            if (null != string) bufferedWriter.write(string);
        }
        bufferedWriter.newLine();
    }

}
