package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.input.MyZipFileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class InMemorySpreadsheetFromCSV implements InMemorySpreadsheet {
    private final String sheetName;
    private final List<String> headers;
    private final List<Row> rows;

    public InMemorySpreadsheetFromCSV(
            final MyZipFileReader myZipFileReader,
            final String pathInZipFile,
            final String sheetName
    ) throws IOException {
        this(myZipFileReader.getInputStreamForReading(pathInZipFile), sheetName);
    }

    public InMemorySpreadsheetFromCSV(final File csvFile, final String sheetName) throws IOException {
        this(new FileInputStream(csvFile), sheetName);
    }

    public InMemorySpreadsheetFromCSV(final InputStream csvStream, final String sheetName) throws IOException {
        this.sheetName = sheetName;
        final CSVFormat csvFormat = CSVFormat.RFC4180.withFirstRecordAsHeader().withTrim(true);
        final CSVParser csvParser = CSVParser.parse(csvStream, Charset.forName("UTF-8"), csvFormat);
        final List<CSVRecord> records = csvParser.getRecords();
        this.headers = Collections.unmodifiableList(csvParser.getHeaderNames());
        this.rows = convert(records);
    }

    private List<Row> convert(final List<CSVRecord> records) {
        final List<Row> retval = new ArrayList<>();
        for(final CSVRecord record : records) {
            final Map<String,String> map = Collections.unmodifiableMap(record.toMap());
            if(!map.isEmpty()) {
                final Row row = new Row(map);
                retval.add(row);
            }
        }
        return Collections.unmodifiableList(retval);
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
        return rows.size();
    }

    @Override
    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public List<String> getSheetNames() {
        return Collections.singletonList(sheetName);
    }
}
