package de.viadee.nexus;

import java.io.File;
import java.io.IOException;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class NexusExport implements Runnable {

    @Option(names = { "-p", "--properties-file" }, description = "Properties file to use")
    private File propertiesFile;

    @Option(names = { "-o", "--output-file" }, description = "Output file to use")
    private File outputFile;

    public static void main(final String[] args) {
	CommandLine.run(new NexusExport(), System.out, args);
    }

    @Override
    public void run() {
	try {
	    if (!propertiesFile.exists()) {
		System.out.println("Das angegebene Properties directory existiert nicht: " + propertiesFile);
		return;
	    }

	    System.out.println("Verwende Properties directory: " + propertiesFile);
	    System.out.println("Zieldate: " + outputFile);

	    new ExportService().propertiesToCSV(propertiesFile, outputFile);
	} catch (final IOException e) {
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

    public void setPropertiesFile(final File propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public File getOutputFile() {
	return outputFile;
    }

    public void setOutputFile(final File outputFile) {
	this.outputFile = outputFile;
    }
}