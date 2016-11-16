package edu.utexas.jdumper.soot;

public enum InstructionKind
{
    NOP(0),
    ASSIGN_CONST(1),
    ASSIGN_ALLOC(2),
    ASSIGN_PHI(3),
    ASSIGN_VAR(4),
    ASSIGN_CAST(5),
    LOAD_INSTANCE(6),
    LOAD_STATIC(7),
    LOAD_ARRAY(8),
    STORE_INSTANCE(9),
    STORE_STATIC(10),
    STORE_ARRAY(11),
    VIRTUAL_INVOKE(12),
    INTERFACE_INVOKE(13),
    STATIC_INVOKE(14),
    SPECIAL_INVOKE(15),
    UNARY_OPERATION(16),
    BINARY_OPERATION(17),
    GOTO(18),
    IF(19),
    LOOKUPSWITCH(20),
    TABLESWITCH(21),
    THROW(22),
    RETURN(23),
    ENTERMONITOR(24),
    EXITMONITOR(25);

    int kid;
    InstructionKind(int i) { kid = i; }
    public int getKindId() { return kid; }
}
