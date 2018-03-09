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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class ExportService {

    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    private boolean isDockerManifestFilled(RepositoryEntry entry) {
	return entry.getDockerManifestName() != null && !entry.getDockerManifestName().equals("");
    }
    
    public void propertiesToCSV(final File root, final File csvFile) throws IOException {
	try (FileOutputStream fileOut = new FileOutputStream(csvFile);
		BufferedOutputStream bos = new BufferedOutputStream(fileOut);
		OutputStreamWriter out = new OutputStreamWriter(bos, "UTF-8")) {
	    final CSVFormat format = CSVFormat.EXCEL.withHeader("Repository Name", "Blob Name", "Size", "Content-Type",
		    "Creation Time", "Deleted", "File", "Maven Group", "Maven Artifact", "Maven Version", "Docker Name",
		    "Docker Version");

	    final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
		    Locale.GERMAN);
	    out.write("sep=,\n");
	    final CSVPrinter printer = format.print(out);

	    List<RepositoryEntry> entries = readEntries(root);
	    Collections.sort(entries);
	    RepositoryEntry lastDockerEntry = null;

	    for (RepositoryEntry entry : entries) {
		
		lastDockerEntry = handleDockerEntries(lastDockerEntry, entry);
		
		try {
		    printer.printRecord(entry.repoName, entry.blobName, entry.size, entry.contentType,
			    dateFormat.format(entry.creationTime), entry.deleted, entry.file.getAbsolutePath(),
			    entry.getMavenGroupName(), entry.getMavenArtifactName(), entry.getMavenVersion(),
			    entry.getDockerManifestName(), entry.getDockerManifestVersion());
		} catch (final IOException ioe) {
		    throw new RuntimeException(ioe);
		}
	    }
	    
	}
    }

    private RepositoryEntry handleDockerEntries(RepositoryEntry lastDockerEntry, RepositoryEntry entry) {
	if (isDockerManifestFilled(entry)) {
	    lastDockerEntry = entry;
	} else {
	    if (entry.isDockerArtifact() && !isDockerManifestFilled(entry)) {
		entry.setExplcitDockerManifestName(lastDockerEntry.getDockerManifestName());
		entry.setExplicitDockerManifestVersion(lastDockerEntry.getDockerManifestVersion());
	    }
	}
	return lastDockerEntry;
    }

    public List<RepositoryEntry> readEntries(File root) {
	final List<RepositoryEntry> list = new ArrayList<>();
	final Consumer<File> consumer = stream -> list.add(readEntry(stream));
	performAction(root, propertiesFileFilter, consumer);
	return list;
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

}
