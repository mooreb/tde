package com.mooreb.tde.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SplitTextFile {
    private final Path fullyJoinedTextFile;
    private final List<String> distinctOrderedSplitFields;
    private final Map<String, Path> fieldToSplitTextFilesMap;
    private final Path pathToSerializedMap;

    public SplitTextFile(
            Path fullyJoinedTextFile,
            List<String> distinctOrderedSplitFields,
            Map<String, Path> fieldToSplitTextFilesMap,
            Path pathToSerializedMap
    ) {
        this.fullyJoinedTextFile = fullyJoinedTextFile;
        this.distinctOrderedSplitFields = distinctOrderedSplitFields;
        this.fieldToSplitTextFilesMap = fieldToSplitTextFilesMap;
        this.pathToSerializedMap = pathToSerializedMap;
    }

    public Path getFullyJoinedTextFile() {
        return fullyJoinedTextFile;
    }

    public List<String> getDistinctOrderedSplitFields() {
        return distinctOrderedSplitFields;
    }

    public Map<String, Path> getFieldToSplitTextFilesMap() {
        return fieldToSplitTextFilesMap;
    }

    public Path getPathToSerializedMap() {
        return pathToSerializedMap;
    }

    public List<Path> getSplitTextFiles() {
        List<Path> retval = new ArrayList<>(distinctOrderedSplitFields.size());
        for(final String field : distinctOrderedSplitFields) {
            final Path path = fieldToSplitTextFilesMap.get(field);
            retval.add(path);
        }
        return Collections.unmodifiableList(retval);
    }
}
