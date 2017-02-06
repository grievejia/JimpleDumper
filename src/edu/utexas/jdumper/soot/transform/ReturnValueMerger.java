package edu.utexas.jdumper.soot.transform;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.util.Chain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A JIMPLE transformation that merges the return values of a method if the said method has more than one return statement.
 */
public class ReturnValueMerger {
    private static List<ReturnStmt> returnUnits(Collection<Unit> units) {
        return units.stream().filter(u -> u instanceof ReturnStmt).map(u -> (ReturnStmt)u).collect(Collectors.toList());
    }

    public static void run(Body methodBody) {
        Chain<Unit> units = methodBody.getUnits();
        List<ReturnStmt> retStmts = returnUnits(units);

        if (retStmts.size() <= 1)
            return;

        // Create the new return stmt first
        Local newRetVar = Jimple.v().newLocal("merged_ret", methodBody.getMethod().getReturnType());
        methodBody.getLocals().add(newRetVar);
        Stmt newRetStmt = Jimple.v().newReturnStmt(newRetVar);
        units.addLast(newRetStmt);

        // Replace the orginal stmt with an assignment and an unconditional jump
        for (ReturnStmt oldRetStmt: retStmts) {
            Stmt assignStmt = Jimple.v().newAssignStmt(newRetVar, oldRetStmt.getOp());
            Stmt gotoStmt = Jimple.v().newGotoStmt(newRetStmt);
            units.insertBefore(assignStmt, oldRetStmt);
            units.insertBefore(gotoStmt, oldRetStmt);
        }
        for (ReturnStmt oldRetStmt: retStmts)
            units.remove(oldRetStmt);
    }
}
