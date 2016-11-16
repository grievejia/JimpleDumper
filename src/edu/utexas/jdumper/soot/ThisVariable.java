package edu.utexas.jdumper.soot;

import soot.SootMethod;

public class ThisVariable extends SootVariable
{
    SootMethod method;

    public ThisVariable(SootMethod method)
    {
        this.method = method;
    }

    public SootMethod getMethod()
    {
        return method;
    }

    @Override
    public String getName()
    {
        return "@this";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThisVariable that = (ThisVariable) o;
        return method == that.method;

    }

    @Override
    public int hashCode()
    {
        return method.hashCode();
    }
}
