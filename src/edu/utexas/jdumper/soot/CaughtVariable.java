package edu.utexas.jdumper.soot;

import soot.jimple.CaughtExceptionRef;

public class CaughtVariable extends SootVariable
{
    CaughtExceptionRef exc;

    public CaughtVariable(CaughtExceptionRef exc)
    {
        this.exc = exc;
    }

    public CaughtExceptionRef getExc()
    {
        return exc;
    }

    @Override
    public String getName()
    {
        return "@caught";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaughtVariable that = (CaughtVariable) o;

        return exc == that.exc;
    }

    @Override
    public int hashCode()
    {
        return exc.hashCode();
    }
}
