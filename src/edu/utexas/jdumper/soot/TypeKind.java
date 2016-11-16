package edu.utexas.jdumper.soot;

public enum TypeKind
{
    NULL(0),
    PRIMITIVE(1),
    CLASS(2),
    INTERFACE(3),
    ARRAY(4);

    int kid;
    TypeKind(int i)
    {
        kid = i;
    }
    public int getKindId() { return kid; }
}
