package com.mooreb.tde.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TextFileStitcherTest {
    @Test(groups = "integration")
    public void stitchTest() throws IOException {
        final Path inputDirectory = Paths.get("src/test/resources/text-file-stitcher");
        final Path outputDirectory = inputDirectory.resolve("output-directory");
        Files.createDirectory(outputDirectory);
        final Path outputFile = inputDirectory.resolve("output-file");
        final Path in1 = inputDirectory.resolve("in-1");
        final Path in2 = inputDirectory.resolve("in-2");
        final Path in3 = inputDirectory.resolve("in-3");
        final Path in4 = inputDirectory.resolve("in-4");
        final Path in5 = inputDirectory.resolve("in-5");
        final List<Path> inputPaths = new ArrayList<>();
        inputPaths.add(in1);
        inputPaths.add(in2);
        inputPaths.add(in3);
        inputPaths.add(in4);
        inputPaths.add(in5);
        final TextFileStitcher textFileStitcher = new TextFileStitcher();
        final StitchedTextFile stitchedTextFile = textFileStitcher.stitch(inputPaths, outputFile, outputDirectory);
        Assert.assertNotNull(stitchedTextFile);
        // BUG: programmatically assert that this class did what I wanted!
    }
}
