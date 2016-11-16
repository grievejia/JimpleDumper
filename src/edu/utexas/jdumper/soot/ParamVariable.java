package edu.utexas.jdumper.soot;

import soot.SootMethod;

public class ParamVariable extends SootVariable
{
    SootMethod method;
    int index;

    public ParamVariable(SootMethod method, int index)
    {
        this.method = method;
        this.index = index;
    }

    @Override
    public String getName()
    {
        return "@param" + index;
    }

    public SootMethod getMethod()
    {
        return method;
    }

    public int getIndex()
    {
        return index;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParamVariable that = (ParamVariable) o;

        if (index != that.index) return false;
        return method == that.method;

    }

    @Override
    public int hashCode()
    {
        int result = method.hashCode();
        result = 31 * result + index;
        return result;
    }
}
