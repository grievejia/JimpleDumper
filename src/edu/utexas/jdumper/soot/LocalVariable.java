package edu.utexas.jdumper.soot;

import soot.Local;

public class LocalVariable extends SootVariable
{
    Local local;

    public LocalVariable(Local local)
    {
        this.local = local;
    }

    @Override
    public String getName()
    {
        return local.getName();
    }

    public Local getLocal()
    {
        return local;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalVariable that = (LocalVariable) o;

        return local == that.local;

    }

    @Override
    public int hashCode()
    {
        return local.hashCode();
    }
}
