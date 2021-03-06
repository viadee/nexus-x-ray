/**
 * BSD 3-Clause License
 *
 * Copyright � 2018, viadee Unternehmensberatung GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

    public ExportService(boolean doPrintSeparatorInFirstLine) {
	this.doPrintSeparatorInFirstLine = doPrintSeparatorInFirstLine;
    }
    
    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    private boolean doPrintSeparatorInFirstLine = false;
    
    private boolean isDockerManifestFilled(RepositoryEntry entry) {
	return entry.getDockerManifestName() != null && !entry.getDockerManifestName().equals("");
    }
    
    public void propertiesToCSV(File[] directoriesToRead, final File csvFile) throws IOException {
	try (FileOutputStream fileOut = new FileOutputStream(csvFile);
		BufferedOutputStream bos = new BufferedOutputStream(fileOut);
		OutputStreamWriter out = new OutputStreamWriter(bos, "UTF-8")) {
	    
	    if (directoriesToRead == null) {
		directoriesToRead = new File[0];
	    }
	    
	    final CSVFormat format = CSVFormat.EXCEL.withHeader("Repository Name", "Blob Name", "Size", "Content-Type",
		    "Creation Time", "Deleted", "File", "Maven Group", "Maven Artifact", "Maven Version", "Docker Name",
		    "Docker Version");

	    final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
		    Locale.GERMAN);
	    if (doPrintSeparatorInFirstLine) {
		out.write("sep=,\n");
	    }
	    final CSVPrinter printer = format.print(out);

	    
	    List<RepositoryEntry> entries = new ArrayList<>();
	    for (File directory : directoriesToRead) {
		entries.addAll(readEntries(directory));
	    }
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

	     File[] newFiles = dir.listFiles(filter);
	    if (newFiles == null) {
		newFiles = new File[0];
	    }
	    Arrays.stream(newFiles).forEach(files::add);
	    System.out.println(newFiles.length + " Datei(en) gefunden.");

	    File[] subDirectories = dir.listFiles(directoryFilter);
	    if (subDirectories == null) {
		subDirectories = new File[0];
	    }
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

    public RepositoryEntry readEntry(final File file) {
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
