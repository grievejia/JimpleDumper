package edu.utexas.jdumper.soot;

public enum ConstantKind
{
    NULLCONST(1),
    INTCONST(2),
    LONGCONST(3),
    FLOATCONST(4),
    DOUBLECONST(5),
    STRCONST(6),
    CLASSCONST(7);

    int kid;
    ConstantKind(int i) { kid = i; }
    public int getKindId() { return kid; }
}
