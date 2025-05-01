package com.mooreb.tde.util;

import java.nio.file.Path;
import java.util.List;

public class StitchedTextFile {
    private final List<Path> originalFiles;
    private final Path stitchedFile;

    public StitchedTextFile(List<Path> originalFiles, Path stitchedFile) {
        this.originalFiles = originalFiles;
        this.stitchedFile = stitchedFile;
    }

    public List<Path> getOriginalFiles() {
        return originalFiles;
    }

    public Path getStitchedFile() {
        return stitchedFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StitchedTextFile that = (StitchedTextFile) o;

        if (originalFiles != null ? !originalFiles.equals(that.originalFiles) : that.originalFiles != null)
            return false;
        return stitchedFile != null ? stitchedFile.equals(that.stitchedFile) : that.stitchedFile == null;
    }

    @Override
    public int hashCode() {
        int result = originalFiles != null ? originalFiles.hashCode() : 0;
        result = 31 * result + (stitchedFile != null ? stitchedFile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StitchedTextFile{" +
                "originalFiles=" + originalFiles +
                ", stitchedFile=" + stitchedFile +
                '}';
    }
}
