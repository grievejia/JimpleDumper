package edu.utexas.jdumper.soot;

import soot.jimple.*;

public enum BinOpKind
{
    ADD(0),
    SUB(1),
    MUL(2),
    DIV(3),
    REM(4),
    AND(5),
    OR(6),
    XOR(7),
    SHL(8),
    SHR(9),
    USHR(10),
    CMP(11),
    CMPG(12),
    CMPL(13),
    EQ(14),
    GE(15),
    GT(16),
    LE(17),
    LT(18),
    NE(19);

    int kid;
    BinOpKind(int i)
    {
        kid = i;
    }
    public int getKindId() { return kid; }

    public static BinOpKind getOpKind(BinopExpr expr)
    {
        if (expr instanceof AddExpr)
            return ADD;
        else if (expr instanceof SubExpr)
            return SUB;
        else if (expr instanceof MulExpr)
            return MUL;
        else if (expr instanceof DivExpr)
            return DIV;
        else if (expr instanceof RemExpr)
            return REM;
        else if (expr instanceof AndExpr)
            return AND;
        else if (expr instanceof OrExpr)
            return OR;
        else if (expr instanceof XorExpr)
            return XOR;
        else if (expr instanceof ShlExpr)
            return SHL;
        else if (expr instanceof ShrExpr)
            return SHR;
        else if (expr instanceof UshrExpr)
            return USHR;
        else if (expr instanceof CmpExpr)
            return CMP;
        else if (expr instanceof CmpgExpr)
            return CMPG;
        else if (expr instanceof CmplExpr)
            return CMPL;
        else if (expr instanceof EqExpr)
            return EQ;
        else if (expr instanceof GeExpr)
            return GE;
        else if (expr instanceof GtExpr)
            return GT;
        else if (expr instanceof LeExpr)
            return LE;
        else if (expr instanceof LtExpr)
            return LT;
        else if (expr instanceof NeExpr)
            return NE;
        else
            throw new RuntimeException("Unknown binary expr: " + expr);
    }
}
