package edu.utexas.jdumper;

import edu.utexas.jdumper.soot.SootLoader;
import edu.utexas.jdumper.writer.JimpleWriter;
import org.apache.commons.cli.*;
import soot.SootClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Parse the command line
        CommandLine cmd = parseCommandLine(args);
        String outfile = getOutputFileName(cmd);

        List<SootClass> classes;
        if (cmd.hasOption("J"))
            classes = SootLoader.loadSootClassesFromJimple(cmd.getOptionValue("J"));
        else
        {
            List<String> appJarList = getApplicationJarList(cmd);
            List<String> libJarList = getLibraryJarList(cmd);
            classes = SootLoader.loadSootClasses(appJarList, libJarList);
        }
        JimpleWriter.writeJimple(outfile, classes);
    }

    private static String getOutputFileName(CommandLine cmd)
    {
        return cmd.getOptionValue("o");
    }

    private static List<String> getLibraryJarList(CommandLine cmd)
    {
       return filterJar(Arrays.asList(cmd.getOptionValue("l").split(":")));
    }

    private static List<String> getApplicationJarList(CommandLine cmd)
    {
        return filterJar(cmd.getArgList());
    }

    private static List<String> filterJar(List<String> list)
    {
        List<String> ret = new ArrayList<>();
        for (String str: list)
        {
            if (str.endsWith(".jar") || str.endsWith(".zip"))
                ret.add(str);
        }
        return ret;
    }

    private static CommandLine parseCommandLine(String[] args)
    {
        Options options = new Options();

        Option readJimple = Option.builder("J")
                .desc("Read the program from a directory of Jimple files")
                .longOpt("jimple-dir")
                .hasArg()
                .build();
        Option sysJars = Option.builder("l")
                .desc("library file(s) used with the application (separated by colons) [REQUIRED]")
                .longOpt("lib")
                .hasArg()
                .build();
        Option out = Option.builder("o")
                .desc("the output file name [REQUIRED]")
                .longOpt("output")
                .hasArg()
                .build();
        Option help = Option.builder("h")
                .desc("Display help message")
                .longOpt("help")
                .build();

        options.addOption(readJimple);
        options.addOption(sysJars);
        options.addOption(out);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        CommandLine ret = null;
        try
        {
            ret = parser.parse(options, args);

            if (ret.hasOption("h"))
            {
                new HelpFormatter().printHelp("JimpleDumper", options, true);
                System.exit(0);
            }
            else if (!ret.hasOption("o"))
            {
                System.err.println("Missing required argument \'-o\'");
                System.exit(-1);
            }
        } catch (ParseException e)
        {
            System.err.println("Command line parsing failed: " + e.getMessage());
            System.exit(-1);
        }
        return ret;
    }
}
