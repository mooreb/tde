package com.mooreb.tde.util.spreadsheet;

import com.mooreb.tde.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MySAXSpreadsheetReader implements XSSFSheetXMLHandler.SheetContentsHandler {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OPCPackage opcPackage;
    private List<Row> listOfRows = new ArrayList<Row>();
    private final Map<String, Boolean> keepWhitespaceForColumnNames;
    private int numRows = 0;
    private Map<String,String> currentRowMap = null;
    private Boolean inHeaders = null;
    private int currentRowOffset = -1;
    private int currentColumnOffset = -1;
    private ArrayList<String> headers = null;
    private ArrayList<String> sheetNames = new ArrayList<String>();
    private List<List<String>> listOfHeaders = new ArrayList<>();
    private List<List<Row>> listOfListOfRows = new ArrayList<>();

    public MySAXSpreadsheetReader(final InputStream inputStream) throws MySAXSpreadsheetReaderException {
        this(inputStream, null);
    }

    public MySAXSpreadsheetReader(final InputStream inputStream,
                                  final Map<String,Boolean> keepWhitespaceForColumnNames
    ) throws MySAXSpreadsheetReaderException {
        try {
            this.opcPackage = OPCPackage.open(inputStream);
        }
        catch(InvalidFormatException|IOException e) {
            LOG.error("caught exception when attemption to OPCPackage.open(inputStream)", e);
            throw new MySAXSpreadsheetReaderException(e);
        }
        this.keepWhitespaceForColumnNames = keepWhitespaceForColumnNames;
        try {
            this.process();
        }
        catch(IOException|OpenXML4JException|SAXException e) {
            LOG.error("caught exception when attemption to call MySaxSpreadsheetReader#process()", e);
            throw new MySAXSpreadsheetReaderException(e);
        }
    }

    public List<String> getSheetNames() { return Collections.unmodifiableList(sheetNames); }

    @Override
    public void startRow(int i) {
        if(0 == i) {
            // assume the first row is headers
            inHeaders = true;
            headers = new ArrayList<String>();
        }
        else {
            inHeaders = false;
            currentRowMap = new HashMap<String,String>();
        }
    }

    @Override
    public void endRow(int i) {
        if(!inHeaders) {
            final Row row = new Row(Collections.unmodifiableMap(currentRowMap));
            listOfRows.add(row);
            numRows++;
        }
    }

    @Override
    public void cell(String cellReferenceString, String formattedContent, XSSFComment xssfComment) {
        // gracefully handle missing CellRef here in a similar way as XSSFCell does
        if(null == cellReferenceString) {
            cellReferenceString = new CellAddress(currentRowOffset, currentColumnOffset).formatAsString();
        }
        final CellReference cellReference = new CellReference(cellReferenceString);
        final short thisCol = cellReference.getCol();
        final int thisRow = cellReference.getRow();
        if(inHeaders) {
            if(0 != thisRow) {
                throw new UnsupportedOperationException("should not be inHeaders in nonzero row");
            }
            if(StringUtils.isEmpty(formattedContent)) {
                throw new UnsupportedOperationException("cannot proceed with a blank header column");
            }
            if(thisCol != (currentColumnOffset+1)) {
                final String fmt = "error parsing %s we appear to have skipped some header columns";
                final String errorMessage = String.format(fmt, cellReferenceString);
                throw new UnsupportedOperationException(errorMessage);
            }
            final String newHeader= formattedContent.trim();
            if(headers.contains(newHeader)) {
                final String fmt = "error in cell %s; we appear to have attempted to reuse an existing header name %s";
                final String errorMessage = String.format(fmt, cellReferenceString, newHeader);
                throw new UnsupportedOperationException(errorMessage);
            }
            headers.add(newHeader);
        }
        else { // not in headers
            final String columnName = headers.get(thisCol);
            if(!StringUtils.isEmpty(formattedContent)) {
                String content = formattedContent;
                boolean keepWhitespace = false;
                if(null != keepWhitespaceForColumnNames) {
                    Boolean override = keepWhitespaceForColumnNames.get(columnName);
                    if(null != override) keepWhitespace = override;
                }
                if(!keepWhitespace) {
                    content = content.trim();
                }
                currentRowMap.put(columnName, content.intern());
            }
        }
        currentColumnOffset = thisCol;
        currentRowOffset = thisRow;
    }

    @Override
    public void headerFooter(String s, boolean b, String s1) {
        // ignore headers and footers
    }

   private void process() throws IOException, OpenXML4JException, SAXException {
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.opcPackage);
        XSSFReader xssfReader = new XSSFReader(this.opcPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
            try (InputStream stream = iter.next()) {
                String sheetName = iter.getSheetName();
                sheetNames.add(sheetName);
                processSheet(styles, strings, this, stream);
                prepareStateForNextSheet();
            }
            ++index;
        }
    }
    private void processSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            XSSFSheetXMLHandler.SheetContentsHandler sheetHandler,
            InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch(ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

    private void prepareStateForNextSheet() {
        this.currentColumnOffset = -1;
        this.currentRowOffset = -1;
        this.numRows = 0;
        listOfHeaders.add(headers);
        headers = null;
        listOfListOfRows.add(listOfRows);
        listOfRows = new ArrayList<Row>();
        inHeaders = null;
    }

    // package-scope
    List<List<Row>> getListOfListsOfRows() {
        return Collections.unmodifiableList(listOfListOfRows);
    }

    // package-scope
    List<List<String>> getListOfHeaders() {
        return Collections.unmodifiableList(listOfHeaders);
    }

}
