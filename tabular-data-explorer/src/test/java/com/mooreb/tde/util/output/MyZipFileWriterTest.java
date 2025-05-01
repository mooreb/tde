package com.mooreb.tde.util.output;

import com.mooreb.tde.util.StringUtils;
import com.mooreb.tde.util.input.MyZipFileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MyZipFileWriterTest {

    @Test(groups = "integration")
    public void writeOne() throws IOException {
        final String fileName = "writer-test-" + StringUtils.myInstant() + ".zip";
        MyZipFileWriter myZipFileWriter = new MyZipFileWriter(fileName);
        final Path myPath = myZipFileWriter.getPathForWriting("writer-test-file");
        final BufferedWriter myBufferedWriter = Files.newBufferedWriter(myPath);
        final Random random = new Random();
        for(int i=0; i<10000000; i++) {
            final long r = random.nextLong();
            myBufferedWriter.write(Long.toString(r));
            myBufferedWriter.newLine();
        }
        myBufferedWriter.flush();
        myBufferedWriter.close();
        myZipFileWriter.finish();
    }

    @Test(groups = "integration")
    public void readOne() throws IOException {
        final String fileName = "writer-test-20180629_003702_427000000-07.zip"; // this is horrible.
        MyZipFileReader myZipFileReader = new MyZipFileReader(fileName);
        BufferedReader bufferedReader = myZipFileReader.getBufferedReaderForReading("writer-test-file");
        int i=0;
        String line;
        while((line = bufferedReader.readLine()) != null) {
            i++;
        }
        Assert.assertTrue(10000000 == i, "expected 10000000 lines");
    }
}
