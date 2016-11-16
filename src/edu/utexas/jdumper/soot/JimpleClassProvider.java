package edu.utexas.jdumper.soot;

import soot.ClassProvider;
import soot.ClassSource;
import soot.JimpleClassSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JimpleClassProvider implements ClassProvider
{
    private Map<String, JimpleClassSource> sourceMap = new HashMap<>();

    public Set<String> getClassNames() {
        return sourceMap.keySet();
    }

    public String addClass(File f) throws IOException {
        String fileName = f.getName();
        if (!fileName.endsWith(".jimple"))
            throw new RuntimeException("Cannot add non-jimple file to JimpleClassProvider!");

        String className = fileName.substring(0, fileName.length() - 7);
        JimpleClassSource classSource = new JimpleClassSource(className, new FileInputStream(f));
        sourceMap.put(className, classSource);
        return className;
    }

    @Override
    public ClassSource find(String s)
    {
        return sourceMap.get(s);
    }
}
