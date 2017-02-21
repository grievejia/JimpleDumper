package edu.utexas.jdumper;

import edu.utexas.jdumper.soot.SootLoader;
import edu.utexas.jdumper.writer.JimpleWriter;
import org.apache.commons.cli.*;
import soot.SootClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Parse the command line
        CommandLine cmd = parseCommandLine(args);
        String outfile = getOutputFileName(cmd);

        List<String> appJarList = getApplicationJarList(cmd);
        List<String> libJarList = getLibraryJarList(cmd);
        List<SootClass> classes = SootLoader.loadSootClasses(appJarList, libJarList, cmd);
        JimpleWriter.writeJimple(outfile, classes, cmd);
    }

    private static String getOutputFileName(CommandLine cmd)
    {
        return cmd.getOptionValue("o");
    }

    private static List<String> getLibraryJarList(CommandLine cmd)
    {
        String libPath = cmd.getOptionValue("l");
        List<String> libList = libPath == null ? Collections.emptyList() : Arrays.asList(libPath.split(":"));
        return libList;
    }

    private static List<String> getApplicationJarList(CommandLine cmd)
    {
        return cmd.getArgList();
    }

    private static CommandLine parseCommandLine(String[] args)
    {
        Options options = new Options();

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
        Option allowPhantom = Option.builder("p")
                .desc("Allow phantom class references")
                .longOpt("allow-phantom")
                .build();
        Option ssa = Option.builder("s")
                .desc("transform the IR into SSA form before dumping")
                .longOpt("ssa")
                .build();
        Option noSSAOpt = Option.builder("d")
                .desc("disable some of the SSA optimization. Use it in case Soot crashes")
                .longOpt("disable-ssa-opt")
                .build();
        Option help = Option.builder("h")
                .desc("Display help message")
                .longOpt("help")
                .build();

        options.addOption(sysJars);
        options.addOption(out);
        options.addOption(ssa);
        options.addOption(noSSAOpt);
        options.addOption(allowPhantom);
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
