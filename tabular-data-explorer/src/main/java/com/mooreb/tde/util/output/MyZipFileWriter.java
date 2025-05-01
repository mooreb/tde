package com.mooreb.tde.util.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MyZipFileWriter {
    private final FileSystem fileSystem;
    private final Path path;

    /** write a zip file of name fileName in the current working directory
     *
     * @param fileName the name of the zip file to write
     * @throws IOException
     */
    public MyZipFileWriter(final String fileName) throws IOException{
        this(Paths.get(".", fileName));
    }

    /** write a new zip file at Path path
     *
     * @param path the fully qualified path of a new zip file
     * @throws IOException
     */
    public MyZipFileWriter(final Path path) throws IOException {
        this.path = path;
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        final URI uri = URI.create("jar:" + path.toUri());
        fileSystem =  FileSystems.newFileSystem(uri, env);
    }

    /** It's the caller's responsibility to write to the returned path
     *
     * @param pathInZipFile the name of the entry in the zip file to create
     * @return the Path that the caller intends to fill with data
     */
    public Path getPathForWriting(final String pathInZipFile) {
        return fileSystem.getPath(pathInZipFile);
    }

    /** Convenience method for somebody (like me) who's new to NIO
     *
     * @param pathInZipFile name of the path in the zip file to write
     * @return a BufferedWriter suitable for writing
     * @throws IOException
     */
    public BufferedWriter getBufferedWriterForWriting(final String pathInZipFile) throws IOException {
        return Files.newBufferedWriter(getPathForWriting(pathInZipFile));
    }

    public void mkdir(final String path) throws IOException {
        Files.createDirectory(getPathForWriting(path));
    }

    public void finish() throws IOException {
        fileSystem.close();
    }

    public Path getPath() {
        return path;
    }
}
