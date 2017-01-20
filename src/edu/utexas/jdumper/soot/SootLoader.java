package edu.utexas.jdumper.soot;

import soot.ClassProvider;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Load all the provided jars into Soot
 */
public class SootLoader
{
    public static void addBasicClass(ClassProvider provider, String className) {
        if(provider.find(className) != null) {
            Scene.v().addBasicClass(className);
        }
    }

    public static List<SootClass> loadSootClasses(List<String> appJarList, List<String> libJarList, boolean allowPhantom)
    {
        NoSearchingClassProvider provider = setNoSearchingClassProvider(appJarList, libJarList);
        return loadClasses(provider, allowPhantom);
    }

    private static List<SootClass> loadClasses(NoSearchingClassProvider provider, boolean allowPhantom)
    {
        // Force resolving all classes
        Options.v().set_full_resolver(true);
        // Do NOT allow any phantom classes
        Options.v().set_allow_phantom_refs(allowPhantom);
        // Keep line number information
        Options.v().set_keep_line_number(true);
        Options.v().set_whole_program(true);

        System.out.println("[JimpleDumper] Loading classes using Soot...");
        Scene scene = Scene.v();
        for (String className: provider.getClassNames())
        {
            scene.loadClass(className, SootClass.SIGNATURES);
            scene.loadClass(className, SootClass.BODIES);
        }
        scene.loadNecessaryClasses();

        System.out.println("[JimpleDumper] All classes loaded");
        return new ArrayList<>(scene.getClasses());
    }

    private static NoSearchingClassProvider setNoSearchingClassProvider(List<String> appJarList, List<String> libJarList)
    {
        NoSearchingClassProvider provider = new NoSearchingClassProvider();
        try
        {
            for (String app: appJarList)
            {
                if (app.endsWith(".jar") || app.endsWith(".zip")) {
                    provider.addArchive(new File(app));
                    System.out.println("[JimpleDumper] Application archive added: " + app);
                }
                else if (app.endsWith(".class")) {
                    provider.addClass(new File(app));
                    System.out.println("[JimpleDumper] Application class added: " + app);
                }
            }
            for (String lib: libJarList)
            {
                if (lib.endsWith(".jar") || lib.endsWith(".zip")) {
                    provider.addArchiveForResolving(new File(lib));
                    System.out.println("[JimpleDumper] Library archive added: " + lib);
                } else {
                    System.err.println("[JimpleDumper] Library file not added (failed to recognize the format): " + lib);
                }
            }
        } catch (IOException e)
        {
            System.err.println("Failed to open jar file: " + e.getMessage());
            System.exit(-1);
        }

        // FileSystem implementation support
        addBasicClass(provider, "java.io.UnixFileSystem");

        // Server application support
        addBasicClass(provider, "sun.net.www.protocol.file.Handler");
        addBasicClass(provider, "sun.net.www.protocol.http.Handler");
        addBasicClass(provider, "sun.net.www.protocol.jar.Handler");
        addBasicClass(provider, "sun.net.www.protocol.http.HttpURLConnection");
//        addBasicClass(provider, "sun.net.www.protocol.ftp.Handler");
//        addBasicClass(provider, "sun.net.www.protocol.https.Handler");
//        addBasicClass(provider, "sun.net.www.protocol.ftp.FtpURLConnection");
//        addBasicClass(provider, "sun.net.www.protocol.https.HttpsURLConnection");

        // Javacard SDK support
        addBasicClass(provider, "javacard.framework.JCSystem");

        soot.SourceLocator.v().setClassProviders(Collections.<ClassProvider>singletonList(provider));
        return provider;
    }
}
