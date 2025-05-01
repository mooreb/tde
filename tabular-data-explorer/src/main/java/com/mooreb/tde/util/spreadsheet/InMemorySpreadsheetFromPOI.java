package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.StringUtils;
import com.mooreb.tde.util.UniqueCategoryTermCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySpreadsheetFromPOI implements InMemorySpreadsheet {
    private final List<Row> listOfRows;
    private final int numRows;
    private final List<String> headers;
    private final List<String> sheetNames;


    // Potential BUG: do not keep whitespace for columns
    // Potential BUG: read only the first sheet
    public InMemorySpreadsheetFromPOI(final String fileName) throws FileNotFoundException, InMemorySpreadsheetException {
        this(new File(fileName));
    }

    // Potential BUG: do not keep whitespace for columns
    // Potential BUG: read only the first sheet
    public InMemorySpreadsheetFromPOI(final File f) throws FileNotFoundException, InMemorySpreadsheetException {
        this(new FileInputStream(f));
    }

    // Potential BUG: do not keep whitespace for columns
    // Potential BUG: read only the first sheet
    public InMemorySpreadsheetFromPOI(final InputStream inputStream) throws InMemorySpreadsheetException {
        this(inputStream, new HashMap<String,Boolean>());
    }

    /** read the first sheet in the workbook
     *
     * @param inputStream
     * @param keepWhitespaceForColumnNames
     * @throws InMemorySpreadsheetException
     */
    public InMemorySpreadsheetFromPOI(final InputStream inputStream,
                               final Map<String,Boolean> keepWhitespaceForColumnNames
    ) throws InMemorySpreadsheetException {
        this(inputStream, keepWhitespaceForColumnNames, 0); // Potential BUG: only read first sheet
    }

    /** read the sheet named "sheetName" in the workbook, dropping leading/trailing whitespace for all columns
     *
     * @param inputStream
     * @param sheetName name of the tab to read
     * @throws InMemorySpreadsheetException
     */
    public InMemorySpreadsheetFromPOI(final InputStream inputStream,
                               final String sheetName
    ) throws InMemorySpreadsheetException {
        this(inputStream, sheetName, null);
    }


    /** read the sheet named "sheetName" in the workbook
     *
     * @param inputStream
     * @param sheetName name of the tab to read
     * @param keepWhitespaceForColumnNames
     * @throws InMemorySpreadsheetException
     */
    public InMemorySpreadsheetFromPOI(final InputStream inputStream,
                               final String sheetName,
                               final Map<String,Boolean> keepWhitespaceForColumnNames
    ) throws InMemorySpreadsheetException {
        try {
            final MySAXSpreadsheetReader mySAXSpreadsheetReader = new MySAXSpreadsheetReader(inputStream, keepWhitespaceForColumnNames);
            sheetNames = mySAXSpreadsheetReader.getSheetNames();
            final int sheetIndex = findSheetIndex(sheetName);
            this.listOfRows = mySAXSpreadsheetReader.getListOfListsOfRows().get(sheetIndex);
            this.numRows = listOfRows.size();
            this.headers = mySAXSpreadsheetReader.getListOfHeaders().get(sheetIndex);
        }
        catch(MySAXSpreadsheetReaderException e) {
            throw new InMemorySpreadsheetException(e);
        }
    }

    private int findSheetIndex(final String desiredSheetName) {
        int i=0;
        for(final String sheetName : sheetNames) {
            if(StringUtils.safeEquals(desiredSheetName, sheetName)) {
                return i;
            }
            i++;
        }
        throw new IllegalArgumentException("cannot find sheet named " + desiredSheetName);
    }

    /**
     *
     * @param inputStream
     * @param keepWhitespaceForColumnNames
     * @param sheetIndex zero-offset; the first sheet is sheetIndex zero
     * @throws InMemorySpreadsheetException
     */
    public InMemorySpreadsheetFromPOI(final InputStream inputStream,
                               final Map<String,Boolean> keepWhitespaceForColumnNames,
                               int sheetIndex
    ) throws InMemorySpreadsheetException {
        try {
            final MySAXSpreadsheetReader mySAXSpreadsheetReader = new MySAXSpreadsheetReader(inputStream, keepWhitespaceForColumnNames);
            sheetNames = mySAXSpreadsheetReader.getSheetNames();
            this.listOfRows = mySAXSpreadsheetReader.getListOfListsOfRows().get(sheetIndex);
            this.numRows = listOfRows.size();
            this.headers = mySAXSpreadsheetReader.getListOfHeaders().get(sheetIndex);
        } catch (MySAXSpreadsheetReaderException e) {
            throw new InMemorySpreadsheetException(e);
        }
    }

    @Override
    public List<Row> getListOfRows() {
        return listOfRows;
    }

    @Override
    public Row getRow(int i) {
        return listOfRows.get(i);
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

    public UniqueCategoryTermCounter<String,String> getUniqueCategoryTermCounter() {
        final UniqueCategoryTermCounter<String,String> counter = new UniqueCategoryTermCounter<String,String>();
        return getUniqueCategoryTermCounter(counter);
    }

    public UniqueCategoryTermCounter<String,String> getUniqueCategoryTermCounter(UniqueCategoryTermCounter<String,String> counter) {
        for(final Row row : listOfRows) {
            for(final String header : headers) {
                final String content = row.getContentsUpper(header);
                counter.put(header, content);
            }
        }
        return counter;
    }

}
