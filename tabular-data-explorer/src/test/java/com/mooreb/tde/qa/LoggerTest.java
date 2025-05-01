package com.mooreb.tde.qa;

import com.mooreb.tde.util.input.MyZipFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class LoggerTest {
    private final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String zipFileName = "src/test/resources/moby-dick.zip";
    private final String plainTextFilenameInZipFile = "moby-dick.txt";
    private final String mobyDick;

    public LoggerTest() throws IOException {
        this.mobyDick = readMobyDickInOneSitting(new MyZipFileReader(zipFileName));
    }

    @Test(groups = "integration")
    public void testLogger() {
        LOG.info("found {} bytes in moby dick", mobyDick.length());
        LOG.info(mobyDick);
    }

    private String readMobyDickInOneSitting(final MyZipFileReader myZipFileReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String line;
        final BufferedReader bufferedReader = myZipFileReader.getBufferedReaderForReading(plainTextFilenameInZipFile);
        while(null != (line = bufferedReader.readLine())) {
            sb.append(line);
            sb.append(" ");
        }
        sb.append("FIN");
        return sb.toString();
    }


}
