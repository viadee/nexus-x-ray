package de.viadee.nexus;

import org.junit.Assert;
import org.junit.Test;

import picocli.CommandLine;

public class CommandLineTest {

   @Test
   public void testCommandLines() {
      String[] args = new String[] {"-p", "d:\\temp\\nexus-properties", "-o", "output.csv" };
      NexusExport instance = new NexusExport();
      CommandLine.run(instance, System.out, args);
      
      Assert.assertNotNull(instance.getOutputFile());
      Assert.assertNotNull(instance.getPropertiesFile());

      
   }
   
   
}
