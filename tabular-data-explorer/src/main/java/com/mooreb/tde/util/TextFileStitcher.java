package com.mooreb.tde.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFileStitcher {
    /**
     *
     * @param filesToStitch the files to stitch together, each one containing a header
     * @param outputFile where to place the contents of the stitched file
     * @param outputDirectory where to move the filesToStitch. Must exist and be a directory before calling #stitch
     * @throws IOException
     */
    public StitchedTextFile stitch(final List<Path> filesToStitch, final Path outputFile, final Path outputDirectory) throws IOException {
        final List<Path> originalFiles = new ArrayList<>();
        checkArguments(filesToStitch, outputDirectory);
        final FileWriter fileWriter = new FileWriter(outputFile.toFile());
        final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        boolean isFirstFile = true;
        for(final Path fileToStitch : filesToStitch) {
            final FileReader fileReader = new FileReader(fileToStitch.toFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final String header = bufferedReader.readLine();
            if (isFirstFile) {
                bufferedWriter.write(header);
                bufferedWriter.newLine();
                isFirstFile = false;
            }
            String line;
            while(null != (line = bufferedReader.readLine())) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            Path destination;
            if(null == outputDirectory) {
                destination = fileToStitch;
            }
            else {
                destination = outputDirectory.resolve(fileToStitch.getFileName());
                Files.move(fileToStitch, destination);
            }
            originalFiles.add(destination);
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return new StitchedTextFile(Collections.unmodifiableList(originalFiles), outputFile);
    }

    private void checkArguments(List<Path> filesToStitch, Path outputDirectory) {
        for(final Path fileToStitch : filesToStitch) {
            if(!Files.exists(fileToStitch)) {
                throw new IllegalArgumentException("expecting fileToStitch to be exist: " + fileToStitch);
            }
        }
        if(null == outputDirectory) return;
        if(!Files.exists(outputDirectory) || !Files.isDirectory(outputDirectory)) {
            throw new IllegalArgumentException("expecting outputDirectory to exist and be a directory: " + outputDirectory);
        }
    }

}
