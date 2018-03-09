package de.viadee.nexus;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "de.viadee.nexus.NexusExport", sortOptions = false,
header = {
	"@|fg(5;4;2)       .__            .___                 |@",
	"@|fg(5;4;2) ___  _|__|____     __| _/____   ____      |@",
	"@|fg(5;4;2) \\  \\/ /  \\__  \\   / __ |/ __ \\_/ __ \\     |@",
	"@|fg(5;4;2)  \\   /|  |/ __ \\_/ /_/ \\  ___/\\  ___/     |@",
	"@|fg(5;4;2)   \\_/ |__(____  /\\____ |\\___  >\\___  >    |@",
	"@|fg(5;4;2)               \\/      \\/    \\/     \\/     |@",
	"@|fg(4;4;3)  _______                                  |@",
	"@|fg(4;4;3)  \\      \\   ____ ___  _____ __  ______    |@",
	"@|fg(4;4;3)  /   |   \\_/ __ \\\\  \\/  /  |  \\/  ___/    |@",
	"@|fg(4;4;3) /    |    \\  ___/ >    <|  |  /\\___ \\     |@",
	"@|fg(4;4;3) \\____|__  /\\___  >__/\\_ \\____//____  >    |@",
	"@|fg(4;4;3)         \\/     \\/      \\/          \\/     |@",
	"@|fg(3;4;2) ____  ___        __________               |@",
	"@|fg(3;5;2) \\   \\/  /        \\______   \\_____  ___.__.|@",
	"@|fg(3;5;2)  \\     /   ______ |       _/\\__  \\<   |  ||@",
	"@|fg(3;5;2)  /     \\  /_____/ |    |   \\ / __ \\\\___  ||@",
	"@|fg(3;5;2) /___/\\  \\         |____|_  /(____  / ____||@",
	"@|fg(3;5;2)       \\_/                \\/      \\/\\/     |@",
	""},
descriptionHeading = "@|bold %nDescription|@:%n",
description = {
        "",
        "Analyze an existing Sonatype Nexus for high disk usage.", },
optionListHeading = "@|bold %nOptions|@:%n",
footer = {
        "ShipIT Day 2018 (viadee)" }
        )
public class NexusExport implements Runnable {

    
    
    private static final Logger logger = LoggerFactory.getLogger(NexusExport.class);

    static final FileFilter propertiesFileFilter = f -> f.isFile() && f.getName().endsWith(".properties");

    @Parameters(arity = "1..*", paramLabel = "DIRECTORY", description = "@|fg(5;4;2) Property directories to use. This is usually something like '/data/nexus_repository/blobs/default/content' |@")
    private File[] propertiesDirectories;

    @Option(names = { "-o", "--output-file" }, description = "@|fg(5;4;2) Output csv-file that should be created.|@")
    private File outputFile;
    
    @Option(arity = "0..1", names = { "-s", "--print-separator" }, description = "@|fg(5;4;2)  Print seperator in first line (useful for MS Excel). |@")
    private boolean doPrintSeparatorInFirstLine = false;

    private ExportService exportService;
    
    public static void main(String[] args) {
	NexusExport instance = new NexusExport();
	
	CommandLine.run(instance, System.err, args);
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