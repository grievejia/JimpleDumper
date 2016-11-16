package edu.utexas.jdumper.soot;

public enum ConstantKind
{
    NULLCONST(1),
    STRCONST(2),
    NUMCONST(3),
    CLASSCONST(4);

    int kid;
    ConstantKind(int i) { kid = i; }
    public int getKindId() { return kid; }
}
