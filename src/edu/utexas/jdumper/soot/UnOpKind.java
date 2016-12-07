package edu.utexas.jdumper.soot;

import soot.jimple.Expr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.LengthExpr;
import soot.jimple.NegExpr;

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

    public static UnOpKind getOpKind(Expr expr)
    {
        if (expr instanceof LengthExpr)
            return LENGTH;
        else if (expr instanceof NegExpr)
            return NEG;
        else if (expr instanceof InstanceOfExpr)
            return INSTANCEOF;
        else
            throw new RuntimeException("Unknown unary expr: " + expr);
    }
}
