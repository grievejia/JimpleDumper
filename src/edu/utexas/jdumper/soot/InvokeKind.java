package edu.utexas.jdumper.soot;

public enum InvokeKind
{
    STATIC(0),
    VIRTUAL(1),
    SPECIAL(2),
    INTERFACE(3);

    int kid;
    InvokeKind(int i)
    {
        kid = i;
    }
    public int getKindId() { return kid; }
    public InstructionKind getInstructionKind()
    {
        switch (this)
        {
            case STATIC:
                return InstructionKind.STATIC_INVOKE;
            case VIRTUAL:
                return InstructionKind.VIRTUAL_INVOKE;
            case SPECIAL:
                return InstructionKind.SPECIAL_INVOKE;
            case INTERFACE:
                return InstructionKind.INTERFACE_INVOKE;
        }
        throw new RuntimeException("Unknown InstructionKind");
    }
}
