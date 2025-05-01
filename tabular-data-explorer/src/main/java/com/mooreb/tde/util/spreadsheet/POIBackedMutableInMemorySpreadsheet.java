package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POIBackedMutableInMemorySpreadsheet implements MutableInMemorySpreadsheet {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String fileName;
    private final SXSSFWorkbook workbook;
    private final CellStyle percentCellStyle;
    private final CellStyle dateCellStyle;
    private List<String> currentHeaders;
    private int currentNumHeaders;
    private SXSSFSheet currentSheet;
    private String currentSheetName;
    private Row currentRow;
    private int currentColNum = 0;
    private int currentRowNum = 0;

    public POIBackedMutableInMemorySpreadsheet(final String fileName) {
        this.fileName = fileName;
        this.workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);

        final DataFormat percentDataFormat = workbook.createDataFormat();
        final short percentDataFormatShort = percentDataFormat.getFormat("0.0000%");
        this.percentCellStyle = workbook.createCellStyle();
        percentCellStyle.setDataFormat(percentDataFormatShort);

        final DataFormat dateDataFormat = workbook.createDataFormat();
        final short dateDataFormatShort = dateDataFormat.getFormat("mm/dd/yyyy");
        this.dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(dateDataFormatShort);
    }

    @Override
    public void addSheet(final List<String> headers, String sheetName) {
        if(null == headers) {
            throw new IllegalArgumentException("cannot have null headers");
        }
        if(null == sheetName) {
            throw new IllegalArgumentException("cannot have null sheet name");
        }
        if(headers.isEmpty()) {
            throw new IllegalArgumentException("cannot have empty headers");
        }

        sheetName = StringUtils.makeSafeForExcelTabName(sheetName);

        this.currentHeaders = headers;
        this.currentNumHeaders = headers.size();
        this.currentSheet = workbook.createSheet(sheetName);
        this.currentSheetName = sheetName;
        currentRowNum = 0;
        currentColNum = 0;
        addRow();
        for(final String header : headers) {
            addCell(header);
        }
    }

    @Override
    public void addRow() {
        underflowWarning();
        currentRow = currentSheet.createRow(currentRowNum++);
        currentColNum = 0;
    }

    private void underflowWarning() {
        if((0 != currentRowNum) && (currentColNum != currentNumHeaders)) {
            LOG.debug("expected more data before calling addRow: currentRowNum: {} currentColNum: {}",
                    currentRowNum, currentColNum, new Exception());
        }
    }

    @Override
    public void addEmptyCell() {
        overflowWarning();
        addStringCellRaw(null);
    }

    @Override
    public void addCell(final String contents) {
        overflowWarning();
        if(StringUtils.isEmpty(contents)) {
            addStringCellRaw(contents);
        }
        else {
            final String[] chunks = StringUtils.split(contents, 32767);
            for(final String chunk : chunks) {
                addStringCellRaw(chunk);
            }
        }
    }

    private void overflowWarning() {
        if(currentColNum >= currentNumHeaders) {
            LOG.warn("expected addRow to be called; more columns than the number of headers: currentRowNum: {} currentColNum: {}",
                    currentRowNum, currentColNum, new Exception());
        }
    }

    // package scope; for testing
    void addStringCellRaw(final String contents) {
        final Cell cell = currentRow.createCell(currentColNum++);
        cell.setCellValue(contents);
    }

    @Override
    public void addCell(final Integer contents) {
        overflowWarning();
        if(null == contents) {
            addEmptyCell();
            return;
        }
        else {
            final Cell cell = currentRow.createCell(currentColNum++);
            cell.setCellValue(contents);
        }
    }

    @Override
    public void addCell(final Double contents) {
        overflowWarning();
        final Cell cell = currentRow.createCell(currentColNum++);
        cell.setCellValue(contents);
    }

    @Override
    public void addPercentageCell(final Double contents) {
        overflowWarning();
        final Cell cell = currentRow.createCell(currentColNum++);
        cell.setCellValue(contents);
        cell.setCellStyle(this.percentCellStyle);
    }

    @Override
    public void addCell(final Date contents) {
        overflowWarning();
        final Cell cell = currentRow.createCell(currentColNum++);
        cell.setCellValue(contents);
        cell.setCellStyle(dateCellStyle);
    }

    @Override
    public void protect() {
        final int numSheets = workbook.getNumberOfSheets();
        for(int i=0; i<numSheets; i++) {
            final SXSSFSheet sheet = workbook.getSheetAt(i);
            sheet.protectSheet("supercalifragilisticexpialidocious");
        }
    }

    /**
     *
     * @param columnIndex column A is offset 0
     * @param width multiply what excel says the width is by 256 to get the proper input
     */
    @Override
    public void setColumnWidth(int columnIndex, int width) {
        currentSheet.setColumnWidth(columnIndex, width);
    }

    @Override
    public File write() throws IOException {
        final File retval = new File(fileName);
        final FileOutputStream fos = new FileOutputStream(retval);
        workbook.write(fos);
        fos.close();
        workbook.dispose();
        return retval;
    }
}
