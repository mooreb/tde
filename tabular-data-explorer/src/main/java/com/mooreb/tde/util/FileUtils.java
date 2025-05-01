package com.mooreb.tde.util;

import com.mooreb.tde.util.input.MyZipFileReader;
import com.mooreb.tde.util.spreadsheet.InMemorySpreadsheet;
import com.mooreb.tde.util.spreadsheet.InMemorySpreadsheetException;
import com.mooreb.tde.util.spreadsheet.InMemorySpreadsheetFromPOI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class FileUtils {
    public static InMemorySpreadsheet fileToInMemorySpreadsheet(final File file)
            throws IOException, InMemorySpreadsheetException
    {
        if(null == file) throw new IllegalArgumentException("file cannot be null");
        final String name = file.getName();
        if(name.endsWith(".xlsx")) return new InMemorySpreadsheetFromPOI(file);
        throw new IllegalArgumentException("I do not know how to process a file named " + name);
    }

    public static InMemorySpreadsheet pathToInMemorySpreadsheet(final Path path)
            throws IOException, InMemorySpreadsheetException
    {
        return fileToInMemorySpreadsheet(path.toFile());
    }

    public static InMemorySpreadsheet nameToInMemorySpreadsheet(final String name)
            throws IOException, InMemorySpreadsheetException
    {
        if(null == name) throw new IllegalArgumentException("name cannot be null");
        final InputStream inputStream = getInputStreamFromName(name);
        if(name.endsWith(".xlsx")) return new InMemorySpreadsheetFromPOI(inputStream);
        throw new IllegalArgumentException("I do not know how to process a file named " + name);
    }

    private static InputStream getInputStreamFromName(final String name) {
        return FileUtils.class.getClassLoader().getResourceAsStream(name);
    }

}
