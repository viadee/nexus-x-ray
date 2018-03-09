package de.viadee.nexus;

import org.junit.Test;

import picocli.CommandLine;

public class CommandLineTest {

   @Test
   public void testCommandLines() {
      String[] args = new String[] {"-p", "d:\\temp\\nexus-properties", "-o", "output.csv" };
      CommandLine.run(new NexusExport(), System.out, args);
   }
   
   
}
