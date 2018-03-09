package de.viadee.nexus;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class NexusExport implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(NexusExport.class);

    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    @Option(names = { "-p", "--properties-file" }, description = "Properties file to use")
    private File propertiesFile;

    @Option(names = { "-o", "--output-file" }, description = "Output file to use")
    private File outputFile;

    private ExportService exportService;
    
    public static void main(String[] args) {
	CommandLine.run(new NexusExport(), System.out, args);
    }

    @Override
    public void run() {
	try {
	    exportService = new ExportService();

	    if (!propertiesFile.exists()) {
		logger.info("Das angegebene Properties directory existiert nicht: " + propertiesFile);
		return;
	    }

	    logger.info("Verwende Properties directory: " + propertiesFile);
	    logger.info("Zieldate: " + outputFile);

	    exportService.propertiesToCSV(propertiesFile, outputFile);
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

    public File getPropertiesFile() {
	return propertiesFile;
    }

    public void setPropertiesFile(File propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public File getOutputFile() {
	return outputFile;
    }

    public void setOutputFile(File outputFile) {
	this.outputFile = outputFile;
    }
}