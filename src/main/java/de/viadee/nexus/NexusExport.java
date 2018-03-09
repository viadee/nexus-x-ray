package de.viadee.nexus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * <pre>
 * find /data/nexus_repository/blobs/default/content -name "*.properties" | tar -cf props.tar.gz -z -T -
 * </pre>
 * 
 * <pre>
 * find /data/nexus_repository/blobs/default/content -type f -size -2048 \( -name "*.properties" -or -name "*.bytes" \) | tar -cvf nexus_repository_small.tar.gz -z -T -
 * </pre>
 */
public class NexusExport {

    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    public static void main(final String[] args) throws IOException {
        final Format format = NumberFormat.getIntegerInstance(Locale.GERMANY);

        final File root = new File("d:\\temp\\nexus-properties");
        final Collection<RepositoryEntry> entries = readEntries(root);

        long size = 0;

        final Map<String, Long> repoNames = new TreeMap<>();
        final Map<String, Long> contentTypes = new TreeMap<>();

        for (final RepositoryEntry entry : entries) {
            System.out.println(entry);
            size += entry.size;
            add(repoNames, entry.repoName, entry.size);
            add(contentTypes, entry.contentType, entry.size);
        }
        // System.out.println(format.format(size) + " Bytes. " + entries.size() + " elements.");
        //
        // for (final Entry<String, Long> repoName : repoNames.entrySet()) {
        // System.out.println(repoName.getKey() + ": " + format.format(repoName.getValue()));
        // }
        //
        // for (final Entry<String, Long> contentType : contentTypes.entrySet()) {
        // System.out.println(contentType.getKey() + ": " + format.format(contentType.getValue()));
        // }
        //
        // for (final RepositoryEntry entry : entries) {
        // if (entry.repoName.contains("docker")) {
        // System.out.println(entry);
        // System.out.print(countSlashes(entry.blobName) + " " + entry.blobName + " ");
        // System.out.println(entry.getMavenVersion());
        // }
        // }
        //
        // propertiesToCSV(root, new File("D:\\temp\\nexus-properties\\entries.csv"));
    }

    private static int countSlashes(final String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '/') {
                count++;
            }
        }
        return count;
    }

    public static void propertiesToCSV(final File root, final File csvFile) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(csvFile);
                BufferedOutputStream bos = new BufferedOutputStream(fileOut);
                OutputStreamWriter out = new OutputStreamWriter(bos, "UTF-8")) {
            final CSVFormat format = CSVFormat.EXCEL.withHeader("Repository Name", "Blob Name", "Size", "Content-Type",
                    "Creation Time", "Deleted", "File", "Maven Group", "Maven Artifact", "Maven Version", "Docker Name",
                    "Docker Version");

            final DateFormat dateFormat = DateFormat.getDateTimeInstance();
            out.write("sep=,\n");
            final CSVPrinter printer = format.print(out);

            final Consumer<File> consumer = w -> {
                final RepositoryEntry entry = readEntry(w);
                try {
                    printer.printRecord(entry.repoName, entry.blobName, entry.size, entry.contentType,
                            dateFormat.format(entry.creationTime), entry.deleted, entry.file.getAbsolutePath(),
                            entry.getMavenGroupName(), entry.getMavenArtifactName(), entry.getMavenVersion(),
                            entry.getDockerManifestName(), entry.getDockerManifestVersion());
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            };

            performAction(root, propertiesFileFilter, consumer);
        }
    }

    public static Collection<RepositoryEntry> readEntries(final File root) {
        final List<RepositoryEntry> list = new ArrayList<>();
        final Consumer<File> consumer = stream -> list.add(readEntry(stream));
        performAction(root, propertiesFileFilter, consumer);
        return list;
    }

    private static void add(final Map<String, Long> map, final String key, final long value) {
        final Long oldValue = map.get(key);
        map.put(key, oldValue == null ? value : value + oldValue);
    }

    public static void performAction(final File root, final FileFilter filter, final Consumer<File> action) {
        final List<File> files = new ArrayList<>();
        final Queue<File> directories = new LinkedList<File>();
        directories.add(root);

        final FileFilter directoryFilter = f -> f.isDirectory();
        while (!directories.isEmpty()) {
            final File dir = directories.poll();
            System.out.println("Durchsuche Verzeichnis " + dir.getAbsolutePath());

            final File[] newFiles = dir.listFiles(filter);
            Arrays.stream(newFiles).forEach(files::add);
            System.out.println(newFiles.length + " Datei(en) gefunden.");

            final File[] subDirectories = dir.listFiles(directoryFilter);
            Arrays.stream(subDirectories).forEach(directories::add);
        }

        int counter = 0;
        for (final File file : files) {
            action.accept(file);
            counter++;
            if (counter % 100 == 0 || counter == files.size()) {
                System.out.println(counter + " von " + files.size() + " Dateien verarbeitet.");
            }
        }
    }

    public static RepositoryEntry readEntry(final File file) {
        try (final FileInputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferIn = new BufferedInputStream(inputStream);
                InputStreamReader reader = new InputStreamReader(bufferIn, "UTF-8")) {
            final Properties props = new Properties();
            props.load(reader);
            final RepositoryEntry entry = new RepositoryEntry();
            entry.file = file;
            entry.size = Long.valueOf(props.getProperty("size", "0"));
            entry.repoName = props.getProperty("@Bucket.repo-name");

            entry.creationTime = new Date(Long.parseLong(props.getProperty("creationTime", "0")));
            entry.blobName = props.getProperty("@BlobStore.blob-name");
            entry.contentType = props.getProperty("@BlobStore.content-type");
            entry.deleted = "true".equals(props.getProperty("deleted"));
            return entry;
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static boolean isMavenArtifact(final RepositoryEntry entry) {
        return !isDockerArtifact(entry);
    }

    private static boolean isDockerArtifact(final RepositoryEntry entry) {
        return entry.repoName.contains("docker");
    }

    public static class RepositoryEntry {

        File file;

        long size;

        String repoName;

        Date creationTime;

        String blobName;

        String contentType;

        boolean deleted;

        @Override
        public String toString() {
            return "RepositoryEntry [blobName=" + blobName + ", repoName=" + repoName + ", size=" + size
                    + ", creationTime=" + creationTime + ", contentType=" + contentType + ", deleted=" + deleted
                    + ", file=" + file.getAbsolutePath() + "]";
        }

        public String getMavenVersion() {
            if (isMavenArtifact(this)) {
                final String[] parts = blobName.split("/");
                return parts.length > 2 ? parts[parts.length - 2] : null;
            } else {
                return null;
            }
        }

        public String getMavenArtifactName() {
            if (isMavenArtifact(this)) {
                final String[] parts = blobName.split("/");
                return parts.length > 3 ? parts[parts.length - 3] : null;
            } else {
                return null;
            }
        }

        public String getMavenGroupName() {
            if (isMavenArtifact(this)) {
                final String[] parts = blobName.split("/");
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length - 3; i++) {
                    if (i > 0) {
                        sb.append('.');
                    }
                    sb.append(parts[i]);
                }
                return sb.toString();
            } else {
                return null;
            }
        }

        public String getDockerManifestName() {
            if (isDockerArtifact(this)) {
                final String[] parts = blobName.split("/");
                if ("manifests".equals(parts[2])) {
                    return parts[1];
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        public String getDockerManifestVersion() {
            if (isDockerArtifact(this)) {
                final String[] parts = blobName.split("/");
                if ("manifests".equals(parts[2])) {
                    return parts[3];
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}