package edu.utexas.jdumper.soot;

public enum UnOpKind
{
    NEG(0),
    LENGTH(1),
    INSTANCEOF(2);

    int kid;
    UnOpKind(int i)
    {
        kid = i;
    }
    public int getKindId() { return kid; }
}
