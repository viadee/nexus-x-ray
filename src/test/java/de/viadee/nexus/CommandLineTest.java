package de.viadee.nexus;

import org.junit.Assert;
import org.junit.Test;

import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;

public class CommandLineTest {

    @Test
    public void testCommandLines() {
	String[] args = new String[] { "-o", "output.csv", "d:\\temp\\nexus-properties" };
	NexusExport instance = new NexusExport();

	CommandLine.run(instance, System.out, args);

	Assert.assertNotNull(instance.getOutputFile());
	Assert.assertNotNull(instance.getPropertiesFiles());

    }
    
    

}
