package com.mooreb.tde.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.ListUtils;

public class TextFileSplitter {
    public SplitTextFile splitByLines(
            final Path fileToSplit,
            final Path intoDirectory,
            final int numLinesExcludingHeader
    ) throws IOException {
        if(null == fileToSplit) throw new IllegalArgumentException("fileToSplit cannot be null");
        if(null == intoDirectory) throw new IllegalArgumentException("intoDirectory cannot be null");
        if(Files.exists(intoDirectory) && !Files.isDirectory(intoDirectory)) {
            throw new IllegalArgumentException("intoDirectory exists but is not a directory");
        }
        if(numLinesExcludingHeader < 1) throw new IllegalArgumentException("numLinesExcludingHeader must be >= 1");
        if(!Files.exists(intoDirectory)) {
            Files.createDirectories(intoDirectory);
        }
        final List<String> allLines = Files.readAllLines(fileToSplit);
        final String header = allLines.remove(0);
        List<List<String>> partitions = ListUtils.partition(allLines, numLinesExcludingHeader);
        int partNumber = 0;
        final List<String> chunkNames = new ArrayList<>();
        final List<Path> chunks = new ArrayList<>();
        for(final List<String> partition : partitions) {
            partNumber++;
            final String chunkName = String.format("chunk-%05d", partNumber);
            chunkNames.add(chunkName);
            final Path chunk = writeFile(intoDirectory, header, partNumber, partition);
            chunks.add(chunk);
        }
        final Map<String, Path> chunkMap = zip(chunkNames, chunks);
        final Path serializedMap = serializeMap(intoDirectory, chunkNames, chunkMap);
        final SplitTextFile retval =
                new SplitTextFile(fileToSplit, chunkNames, chunkMap, serializedMap);
        return retval;
    }

    public SplitTextFile splitByColumn(
            final Path fileToSplit,
            final Path intoDirectory,
            final String delimiter,
            final int oneOffsetPosition
    ) throws IOException {
        if(null == fileToSplit) throw new IllegalArgumentException("fileToSplit cannot be null");
        if(null == intoDirectory) throw new IllegalArgumentException("intoDirectory cannot be null");
        if(Files.exists(intoDirectory) && !Files.isDirectory(intoDirectory)) {
            throw new IllegalArgumentException("intoDirectory exists but is not a directory");
        }
        if(null == delimiter) throw new IllegalArgumentException("delimiter cannot be null");
        if(oneOffsetPosition <= 0) throw new IllegalArgumentException("oneOffsetPosition must be positive: " + oneOffsetPosition);

        if(!Files.exists(intoDirectory)) {
            Files.createDirectories(intoDirectory);
        }

        final int zeroOffsetPosition = oneOffsetPosition - 1;
        final List<String> allLines = Files.readAllLines(fileToSplit);
        final String header = allLines.remove(0);
        final Set<String> seenSet = new HashSet<>();
        final List<String> seenList = new ArrayList<>();
        final MultiMap<String, String> partsToLines = new MutableMultiMap<>();
        for(final String line : allLines) {
            final String[] parts = line.split(delimiter);
            final String part = parts[zeroOffsetPosition];
            if(!seenSet.contains(part)) {
                seenSet.add(part);
                seenList.add(part);
            }
            partsToLines.put(part, line);
        }
        final List<String> parts = Collections.unmodifiableList(seenList);
        final List<Path> paths = writeFiles(intoDirectory, header, parts, partsToLines);
        final Map<String, Path> partsToPaths = zip(parts, paths);
        final Path serializedMap = serializeMap(intoDirectory, parts, partsToPaths);
        final SplitTextFile retval =
                new SplitTextFile(fileToSplit, parts, partsToPaths, serializedMap);
        return retval;
    }

    private List<Path> writeFiles(
            final Path intoDirectory,
            final String header,
            final List<String> parts,
            final MultiMap<String, String> partsToLines
    ) throws IOException {
        final List<Path> retval = new ArrayList<>();
        int partNumber = 0;
        for(final String part : parts) {
            final List<String> lines = partsToLines.get(part);
            final Path path = writeFile(intoDirectory, header, ++partNumber, lines);
            retval.add(path);
        }
        return Collections.unmodifiableList(retval);
    }

    private Path writeFile(
            final Path intoDirectory,
            final String header,
            final int partNumber,
            final List<String> lines) throws IOException {
        final String partName = String.format("part-%05d.txt", partNumber);
        final Path path = intoDirectory.resolve(partName);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
        bufferedWriter.write(header);
        bufferedWriter.newLine();
        for(final String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return path;
    }

    private Path serializeMap(
            final Path intoDirectory,
            final List<String> parts,
            final Map<String, Path> partsToPaths
    ) throws IOException {
        final String mapName = "serialized-map-" + StringUtils.myInstant() + ".txt";
        final Path serializedMapPath = intoDirectory.resolve(mapName);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(serializedMapPath);
        for(final String part : parts) {
            final Path path = partsToPaths.get(part);
            bufferedWriter.write(part);
            bufferedWriter.write("\t");
            bufferedWriter.write(path.toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return serializedMapPath;
    }

    private Map<String, Path> zip(final List<String> parts, final List<Path> paths) {
        final Map<String, Path> retval = new HashMap<>();
        final int n = parts.size();
        for (int i = 0; i < n; i++) {
            final String part = parts.get(i);
            final Path path = paths.get(i);
            retval.put(part, path);
        }
        return Collections.unmodifiableMap(retval);
    }

    public Path join(final SplitTextFile splitTextFile, final Path desiredJoinedPath) throws IOException {
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(desiredJoinedPath);
        final List<String> parts = splitTextFile.getDistinctOrderedSplitFields();
        final Map<String, Path> map = splitTextFile.getFieldToSplitTextFilesMap();
        boolean wroteHeader = false;
        for(final String part : parts) {
            final Path path = map.get(part);
            final List<String> lines = Files.readAllLines(path);
            final String header = lines.remove(0);
            if(!wroteHeader) {
                bufferedWriter.write(header);
                bufferedWriter.newLine();
                wroteHeader = true;
            }
            for(final String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return desiredJoinedPath;
    }

    public SplitTextFile fromMap(final Path serializedMap) throws IOException {
        final List<String> lines = Files.readAllLines(serializedMap);
        final int numDistinctFields = lines.size();
        final List<String> fields = new ArrayList<>(numDistinctFields);
        final Map<String, Path> fieldsToPathMap = new HashMap<>();
        for(final String line : lines) {
            final String[] fragments = line.split("\t", 2);
            final String field = fragments[0];
            final String pathString = fragments[1];
            final Path path = Paths.get(pathString);
            fields.add(field);
            fieldsToPathMap.put(field, path);
        }
        final List<String> immutableFields = Collections.unmodifiableList(fields);
        final Map<String, Path> immutableFieldsToPathMap = Collections.unmodifiableMap(fieldsToPathMap);
        final SplitTextFile retval =
                new SplitTextFile(null, immutableFields, immutableFieldsToPathMap, serializedMap);
        return retval;
    }

}
