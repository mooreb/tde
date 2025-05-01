package com.mooreb.tde.util.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyZipFileReader {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private FileSystem fileSystem;
    // There's a race here because the underlying provider (com.sun.nio.zipfs.ZipFileSystemProvider)
    //   throws FileSystemAlreadyExistsException for attempts to open a zip file on disk more than once
    // IMO: this probably shouldn't be the case for zip files on disk, but
    //     com.sun.nio.zipfs.ZipFileSystemProvider
    //   disagrees, and
    //     com.sun.nio.zipfs.ZipFileSystem
    //   complies by checking if the underlying FileSystem is closed.
    // In any event the semantics of java.nio.file.spi.FileSystemProvider#newFileSystem encourage
    //   throwing FileSystemAlreadyExistsException (a RuntimeException) in the documentation, though
    //   the signature for #newFileSystem declares only throws IOException
    // The underlying FileSystem is shared.
    // Don't try to clean up if closeFileSytemOnFinish is false.
    // https://bugs.openjdk.java.net/browse/JDK-8223151
    // https://bugs.openjdk.java.net/browse/JDK-8223771
    private static final boolean closeFileSytemOnFinish = false;

    public MyZipFileReader(final String fileName) throws IOException {
        this(Paths.get(".", fileName));
    }

    public MyZipFileReader(final File file) throws IOException {
        this(file.toPath());
    }

    /** read a zip file at Path path
     *
     * @param path the fully qualified path of a new zip file
     * @throws IOException
     */
    public MyZipFileReader(final Path path) throws IOException {
        LOG.info("starting to initialize MyZipFileReader at path {}", path /*, new Exception("here") */);
        final Map<String, String> env = new HashMap<>();
        final URI uri = URI.create("jar:" + path.toUri());
        try {
            fileSystem = FileSystems.newFileSystem(uri, env);
        }
        catch(FileSystemAlreadyExistsException e) {
            LOG.info("zip filesystem {} already exists; attempting to get it", path);
            fileSystem = FileSystems.getFileSystem(uri);
        }
        LOG.info("finished initializing MyZipFileReader at path {}", path);
    }

    public BufferedReader getBufferedReaderForReading(final String pathInZipFile) throws IOException {
        return Files.newBufferedReader(getPathForReading(pathInZipFile));
    }

    public InputStream getInputStreamForReading(final String pathInZipFile) throws IOException {
        return Files.newInputStream(getPathForReading(pathInZipFile));
    }

    private Path getPathForReading(final String pathInZipFile) {
        return fileSystem.getPath(pathInZipFile);
    }

    public List<Path> getAllRegularFiles() throws IOException {
        final List<Path> retval = new ArrayList<>();
        final BiPredicate<Path, BasicFileAttributes> isRegularFile =  (p, bfa) -> bfa.isRegularFile();
        for(final Path aRoot : fileSystem.getRootDirectories()) {
            final Stream<Path> regularFiles = Files.find(aRoot, Integer.MAX_VALUE, isRegularFile);
            final Iterator<Path> regularFilesIterator = regularFiles.iterator();
            while(regularFilesIterator.hasNext()) {
                final Path path = regularFilesIterator.next();
                retval.add(path);
            }
        }
        return retval;
    }

    public void finish() throws IOException {
        if(closeFileSytemOnFinish) {
            fileSystem.close();
        }
    }

    @Override
    public String toString() {
        return "MyZipFileReader{" +
                "fileSystem=" + fileSystem +
                '}';
    }
}
