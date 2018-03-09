package de.viadee.nexus;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class NexusExport implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(NexusExport.class);

    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "Property directories to use")
    private File[] propertiesDirectories;

    @Option(names = { "-o", "--output-file" }, description = "Output file to use")
    private File outputFile;
    
    @Option(arity = "0..1", names = { "-s", "--print-separator" }, description = "Print separtor in first line.")
    private boolean doPrintSeparatorInFirstLine = false;

    private ExportService exportService;
    
    public static void main(String[] args) {
	CommandLine.run(new NexusExport(), System.out, args);
    }

    @Override
    public void run() {
	try {
	    exportService = new ExportService(doPrintSeparatorInFirstLine);

	    logger.info("Verwende Properties directories: " + propertiesDirectories);
	    logger.info("Zieldate: " + outputFile);

	    exportService.propertiesToCSV(propertiesDirectories, outputFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

   

    public static boolean isMavenArtifact(final RepositoryEntry entry) {
	return !isDockerArtifact(entry);
    }

    private static boolean isDockerArtifact(final RepositoryEntry entry) {
	return entry.repoName.contains("docker");
    }

    public File[] getPropertiesFiles() {
	return propertiesDirectories;
    }

    public void setPropertiesFiles(File[] propertiesDirectories) {
	this.propertiesDirectories = propertiesDirectories;
    }

    public File getOutputFile() {
	return outputFile;
    }

    public void setOutputFile(File outputFile) {
	this.outputFile = outputFile;
    }
}