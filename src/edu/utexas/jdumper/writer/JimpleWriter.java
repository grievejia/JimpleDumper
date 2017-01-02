package edu.utexas.jdumper.writer;

import edu.utexas.jdumper.soot.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.tagkit.LineNumberTag;

import java.sql.*;
import java.util.*;

/**
 * Serialize JIMPLE to disk
 */
public final class JimpleWriter
{
    private static class IfInfo
    {
        int stmtId;
        int conditionId;

        public IfInfo(int stmtId, int conditionId)
        {
            this.stmtId = stmtId;
            this.conditionId = conditionId;
        }
    }

    DatabaseWriter dbWriter;
    private IndexMap<Type> typeMap = new IndexMap<>();
    private IndexMap<SootField> fieldMap = new IndexMap<>();
    private IndexMap<SootMethod> methodMap = new IndexMap<>();
    private IndexMap<SootVariable> varMap = new IndexMap<>();
    private IndexMap<Constant> constMap = new IndexMap<>();
    private IndexMap<Stmt> stmtMap = new IndexMap<>();
    private Map<IfStmt, IfInfo> ifMap = new HashMap<>();
    private Map<LookupSwitchStmt, Integer> lookupSwitchMap = new HashMap<>();
    private Map<TableSwitchStmt, Integer> tableSwitchMap = new HashMap<>();
    private IndexMap<Trap> trapMap = new IndexMap<>();

    private JimpleWriter(Connection connection) throws SQLException
    {
        dbWriter = new DatabaseWriter(connection);
    }

    private int getTypeId(Type type) throws SQLException
    {
        // Array type requires special handling here since their table are not populated at the beginning of the dump process
        if (type instanceof ArrayType)
        {
            ArrayType aType = (ArrayType) type;
            if (!typeMap.exist(aType))
            {
                int elemId = getTypeId(aType.getElementType());
                int id = typeMap.getIndex(aType);
                dbWriter.writeArrayType(id, elemId);
                return id;
            }
        }
        return typeMap.getIndexOrFail(type);
    }

    private int getConstantId(Constant constant) throws SQLException
    {
        if (!constMap.exist(constant))
        {
            int id = constMap.getIndex(constant);
            if (constant instanceof IntConstant)
            {
                IntConstant intConst = (IntConstant) constant;
                dbWriter.writeIntConstant(id, intConst.value);
            }
            else if (constant instanceof LongConstant)
            {
                LongConstant longConst = (LongConstant) constant;
                dbWriter.writeLongConstant(id, longConst.value);
            }
            else if (constant instanceof FloatConstant)
            {
                FloatConstant floatConst = (FloatConstant) constant;
                dbWriter.writeFloatConstant(id, floatConst.value);
            }
            else if (constant instanceof DoubleConstant)
            {
                DoubleConstant doubleConst = (DoubleConstant) constant;
                dbWriter.writeDoubleConstant(id, doubleConst.value);
            }
            else if (constant instanceof NullConstant)
            {
                dbWriter.writeNullConstant(id);
            }
            else if (constant instanceof StringConstant)
            {
                StringConstant strConst = (StringConstant) constant;
                dbWriter.writeStringConstant(id, strConst.value);
            }
            else if (constant instanceof ClassConstant)
            {
                ClassConstant classConst = (ClassConstant) constant;
                dbWriter.writeClassConstant(id, classConst.value);
            }
            else
                throw new RuntimeException("Unsupported constant: " + constant);
            return id;
        }
        return constMap.getIndexOrFail(constant);
    }

    private void writeNullType() throws SQLException
    {
        int id = typeMap.getIndex(soot.NullType.v());
        dbWriter.writeNullType(id);
    }

    private void writePrimitiveTypes() throws SQLException
    {
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.BooleanType.v()), "boolean");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.ByteType.v()), "byte");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.CharType.v()), "char");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.ShortType.v()), "short");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.IntType.v()), "int");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.LongType.v()), "long");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.FloatType.v()), "float");
        dbWriter.writePrimitiveType(typeMap.getIndex(soot.DoubleType.v()), "double");
    }

    private void writeField(SootField field) throws SQLException {
        int fid = fieldMap.getIndex(field);
        String name = field.getName();
        int tid = getTypeId(field.getType());
        int parentId = getTypeId(field.getDeclaringClass().getType());
        int modifier = field.getModifiers();
        dbWriter.writeField(fid, name, tid, parentId, modifier);
    }

    private void writeFields(SootClass cl) throws SQLException
    {
        for (SootField field: cl.getFields())
            writeField(field);
    }

    private int writeLocal(int mid, Local local) throws SQLException
    {
        SootVariable var = new LocalVariable(local);
        int vid = varMap.getIndex(var);
        int tid = getTypeId(local.getType());
        dbWriter.writeVariable(vid, var.getName(), tid, mid);
        return vid;
    }

    private void writeLocals(int mid, Body body) throws SQLException
    {
        for (Local local: body.getLocals())
            writeLocal(mid, local);
    }

    private void writeAssignToLocal(int mid, AssignStmt stmt, int lhsId) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value rhs = stmt.getRightOp();
        if (rhs instanceof Local)
        {
            Local local = (Local) rhs;
            int rhsId = varMap.getIndex(new LocalVariable(local));
            dbWriter.writeAssignVariableInstruction(id, lhsId, rhsId, mid);
        }
        else if (rhs instanceof InvokeExpr)
        {
            InvokeExpr expr = (InvokeExpr) rhs;
            LineNumberTag tag = (LineNumberTag) stmt.getTag("LineNumberTag");
            writeInvokeExpr(id, expr, mid, tag == null ? null : tag.getLineNumber(), lhsId);
        }
        else if (rhs instanceof CastExpr)
        {
            CastExpr cast = (CastExpr) rhs;
            Value op = cast.getOp();

            if (op instanceof Local)
            {
                int rhsId = varMap.getIndex(new LocalVariable((Local) op));
                int typeId = getTypeId(cast.getCastType());
                dbWriter.writeAssignCastInstruction(id, lhsId, rhsId, typeId, mid);
            }
            else if (op instanceof Constant)
            {
                int cid = getConstantId((Constant) op);
                dbWriter.writeAssignConstInstruction(id, lhsId, cid, mid);
            }
            else
                throw new RuntimeException("Unsupported assign cast stmt: " + stmt);
        }
        else if (rhs instanceof NewExpr)
        {
            NewExpr newExpr = (NewExpr) rhs;
            int tid = typeMap.getIndexOrFail(newExpr.getType());
            dbWriter.writeAssignAllocInstruction(id, lhsId, tid, mid);
        }
        else if (rhs instanceof NewArrayExpr)
        {
            NewArrayExpr newExpr = (NewArrayExpr) rhs;
            int tid = getTypeId(newExpr.getType());

            Value sizeVal = newExpr.getSize();
            int vid;
            if (sizeVal instanceof Local)
            {
                Local local = (Local) sizeVal;
                vid = varMap.getIndex(new LocalVariable(local));
            }
            else if (sizeVal instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) sizeVal;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                vid = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported alloc size value: " + sizeVal);

            dbWriter.writeAllocSize(vid, 0, id);
            dbWriter.writeAssignAllocInstruction(id, lhsId, tid, mid);
        }
        else if (rhs instanceof NewMultiArrayExpr)
        {
            NewMultiArrayExpr newExpr = (NewMultiArrayExpr) rhs;
            int tid = getTypeId(newExpr.getType());

            ArrayList<Integer> sizeList = new ArrayList<>();
            for (int i = 0, e = newExpr.getSizeCount(); i < e; ++i) {
                Value sizeVal = newExpr.getSize(i);
                int vid;
                if (sizeVal instanceof Local)
                {
                    Local local = (Local) sizeVal;
                    vid = varMap.getIndex(new LocalVariable(local));
                }
                else if (sizeVal instanceof Constant)
                {
                    // Fabricate an ASSIGN_CONST instruction here
                    Constant c = (Constant) sizeVal;
                    int cid = getConstantId(c);
                    Local l = new JimpleLocal("const" + id, c.getType());
                    vid = writeLocal(mid, l);
                    dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

                    id = stmtMap.getNextIndex();
                }
                else
                    throw new RuntimeException("Unsupported alloc size value: " + sizeVal);
                sizeList.add(vid);
            }

            dbWriter.writeAssignAllocInstruction(id, lhsId, tid, mid);
            for (int i = 0, e = sizeList.size(); i < e; ++i) {
                Integer vid = sizeList.get(i);
                dbWriter.writeAllocSize(vid, i, id);
            }
        }
        else if (rhs instanceof Constant)
        {
            int cid = getConstantId((Constant) rhs);
            dbWriter.writeAssignConstInstruction(id, lhsId, cid, mid);
        }
        else if (rhs instanceof InstanceFieldRef)
        {
            InstanceFieldRef iFieldRef = (InstanceFieldRef) rhs;
            Local local = (Local) iFieldRef.getBase();

            SootField field = iFieldRef.getField();
            if (!fieldMap.exist(field)) {
                System.err.println("[JimpleDumper] Warning: Cannot find instance field " + field.getSignature());
                writeField(field);
            }
            int fieldId = fieldMap.getIndexOrFail(iFieldRef.getField());

            int rhsId = varMap.getIndex(new LocalVariable(local));
            dbWriter.writeLoadInstanceFieldInstruction(id, lhsId, rhsId, fieldId, mid);
        }
        else if (rhs instanceof StaticFieldRef)
        {
            StaticFieldRef sFieldRef = (StaticFieldRef) rhs;
            SootField field = sFieldRef.getField();
            if (!fieldMap.exist(field)) {
                System.err.println("[JimpleDumper] Warning: Cannot find static field " + field.getSignature());
                writeField(field);
            }
            int fieldId = fieldMap.getIndexOrFail(field);
            dbWriter.writeLoadStaticFieldInstruction(id, lhsId, fieldId, mid);
        }
        else if (rhs instanceof ArrayRef)
        {
            ArrayRef arrayRef = (ArrayRef) rhs;
            Local base = (Local) arrayRef.getBase();
            int baseId = varMap.getIndex(new LocalVariable(base));
            Value index = arrayRef.getIndex();
            int iid;
            if (index instanceof Local)
            {
                Local local = (Local) index;
                iid = varMap.getIndex(new LocalVariable(local));
            }
            else if (index instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) index;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                iid = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, iid, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported operand: " + index);

            dbWriter.writeLoadArrayInstruction(id, lhsId, baseId, iid, mid);
        }
        else if (rhs instanceof BinopExpr)
        {
            BinopExpr expr = (BinopExpr) rhs;
            BinOpKind kind = BinOpKind.getOpKind(expr);
            Value op0 = expr.getOp1();
            int op0Id;
            if (op0 instanceof Local)
            {
                Local local = (Local) op0;
                op0Id = varMap.getIndex(new LocalVariable(local));
            }
            else if (op0 instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) op0;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                op0Id = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, op0Id, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported operand: " + op0);

            Value op1 = expr.getOp2();
            int op1Id;
            if (op1 instanceof Local)
            {
                Local local = (Local) op1;
                op1Id = varMap.getIndex(new LocalVariable(local));
            }
            else if (op1 instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) op1;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                op1Id = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, op1Id, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported operand: " + op1);

            dbWriter.writeBinaryOpInstruction(id, kind, lhsId, op0Id, op1Id, mid);
        }
        else if (rhs instanceof NegExpr || rhs instanceof LengthExpr || rhs instanceof InstanceOfExpr)
        {
            Value op = null;
            UnOpKind kind;
            if (rhs instanceof NegExpr) {
                kind = UnOpKind.NEG;
                op = ((NegExpr) rhs).getOp();
            }
            else if (rhs instanceof LengthExpr) {
                kind = UnOpKind.LENGTH;
                op = ((LengthExpr) rhs).getOp();
            }
            else if (rhs instanceof InstanceOfExpr) {
                kind = UnOpKind.INSTANCEOF;
                op = ((InstanceOfExpr) rhs).getOp();
            } else {
                throw new RuntimeException("Not an unary exp: " + rhs);
            }

            int opId;
            if (op instanceof Local)
            {
                Local local = (Local) op;
                opId = varMap.getIndex(new LocalVariable(local));
            }
            else if (op instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) op;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                opId = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, opId, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported operand: " + op);

            dbWriter.writeUnaryOpInstruction(id, kind, lhsId, opId, mid);
        }
        else if (rhs instanceof PhiExpr)
        {
            PhiExpr rhsPhi = (PhiExpr) rhs;
            // ShimpleBody sometimes gives me phiexpr with duplicated rhs.
            // This is annoying, but we have to deal with it.
            Set<Integer> rhsIds = new TreeSet<>();
            for (Value rhsVal: rhsPhi.getValues())
            {
                if (rhsVal instanceof Local)
                {
                    Local rhsLocal = (Local) rhsVal;
                    int rhsId = varMap.getIndex(new LocalVariable(rhsLocal));
                    rhsIds.add(rhsId);
                }
                else
                    throw new RuntimeException("Unsupported PHI rhs: " + rhsVal);
            }
            dbWriter.writeAssignPhiInstruction(id, lhsId, new ArrayList<>(rhsIds), mid);
        }
        else
        {
            throw new RuntimeException("Unsupported assignment stmt: " + stmt);
        }
    }

    private void writeAssignToNonLocal(int mid, AssignStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);

        Value lhs = stmt.getLeftOp();
        Value rhs = stmt.getRightOp();
        int rhsId;
        if (rhs instanceof Local)
        {
            Local local = (Local) rhs;
            rhsId = varMap.getIndex(new LocalVariable(local));
        }
        else if (rhs instanceof Constant)
        {
            // Fabricate an ASSIGN_CONST instruction here
            Constant c = (Constant) rhs;
            int cid = getConstantId(c);
            Local l = new JimpleLocal("const" + id, c.getType());
            rhsId = writeLocal(mid, l);
            dbWriter.writeAssignConstInstruction(id, rhsId, cid, mid);

            id = stmtMap.getNextIndex();
        }
        else
            throw new RuntimeException("Unsupported assignment rhs: " + rhs);

        if (lhs instanceof InstanceFieldRef)
        {
            InstanceFieldRef iFieldRef = (InstanceFieldRef) lhs;
            Local base = (Local) iFieldRef.getBase();
            SootField field = iFieldRef.getField();
            if (!fieldMap.exist(field)) {
                System.err.println("[JimpleDumper] Warning: Cannot find instance field " + field.getSignature());
                writeField(field);
            }
            int fieldId = fieldMap.getIndexOrFail(field);
            int baseId = varMap.getIndex(new LocalVariable(base));
            dbWriter.writeStoreInstanceFieldInstruction(id, baseId, fieldId, rhsId, mid);
        }
        else if (lhs instanceof StaticFieldRef)
        {
            StaticFieldRef sFieldRef = (StaticFieldRef) lhs;
            SootField field = sFieldRef.getField();
            if (!fieldMap.exist(field)) {
                System.err.println("[JimpleDumper] Warning: Cannot find static field " + field.getSignature());
                writeField(field);
            }
            int fieldId = fieldMap.getIndexOrFail(field);
            dbWriter.writeStoreStaticFieldInstruction(id, fieldId, rhsId, mid);
        }
        else if (lhs instanceof ArrayRef)
        {
            ArrayRef arrayRef = (ArrayRef) lhs;
            Local base = (Local) arrayRef.getBase();
            int baseId = varMap.getIndex(new LocalVariable(base));
            Value index = arrayRef.getIndex();
            int iid;
            if (index instanceof Local)
            {
                Local local = (Local) index;
                iid = varMap.getIndex(new LocalVariable(local));
            }
            else if (index instanceof Constant)
            {
                // Fabricate an ASSIGN_CONST instruction here
                Constant c = (Constant) index;
                int cid = getConstantId(c);
                Local l = new JimpleLocal("const" + id, c.getType());
                iid = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, iid, cid, mid);

                id = stmtMap.getNextIndex();
            }
            else
                throw new RuntimeException("Unsupported operand: " + index);
            dbWriter.writeStoreArrayInstruction(id, baseId, iid, rhsId, mid);
        }
        else
            throw new RuntimeException("Unsupported assignment lhs: " + lhs);
    }

    private void writeAssignStmt(int mid, AssignStmt stmt) throws SQLException
    {
        Value lhs = stmt.getLeftOp();
        if (lhs instanceof Local)
        {
            Local local = (Local) lhs;
            int vid = varMap.getIndex(new LocalVariable(local));
            writeAssignToLocal(mid, stmt, vid);
        }
        else
            writeAssignToNonLocal(mid, stmt);
    }

    private void writeIdentityStmt(int mid, SootMethod method, IdentityStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value lhs = stmt.getLeftOp();
        Value rhs = stmt.getRightOp();

        if(rhs instanceof CaughtExceptionRef) {
            // The case is handled by ExceptionHandler writer
            // Pretend that stmt is an no-op here
            dbWriter.writeNopInstruction(id, mid);
        }
        else if(lhs instanceof Local && rhs instanceof ThisRef)
        {
            Local local = (Local) lhs;
            int lhsId = varMap.getIndex(new LocalVariable(local));
            int rhsId = varMap.getIndex(new ThisVariable(method));
            dbWriter.writeAssignVariableInstruction(id, lhsId, rhsId, mid);
        }
        else if(lhs instanceof Local && rhs instanceof ParameterRef)
        {
            Local local = (Local) lhs;
            ParameterRef parameterRef = (ParameterRef) rhs;
            int lhsId = varMap.getIndex(new LocalVariable(local));
            int rhsId = varMap.getIndex(new ParamVariable(method, parameterRef.getIndex()));
            dbWriter.writeAssignVariableInstruction(id, lhsId, rhsId, mid);
        }
        else
        {
            throw new RuntimeException("Unsupported identity statement: " + stmt);
        }
    }

    private void writeInvokeExpr(int id, InvokeExpr expr, int mid, Integer lineno, Integer retId) throws SQLException
    {
        // Write actual arguments
        ArrayList<Integer> argIdList = new ArrayList<>();
        for (int i = 0; i < expr.getArgCount(); ++i)
        {
            Value v = expr.getArg(i);
            if (v instanceof Local)
            {
                Local local = (Local) v;
                int vid = varMap.getIndexOrFail(new LocalVariable(local));
                argIdList.add(vid);
            }
            else if (v instanceof Constant)
            {
                Constant c = (Constant) v;
                int cid = getConstantId(c);

                // Fabricate an ASSIGN_CONST instruction here
                Local l = new JimpleLocal("const" + id, c.getType());
                int vid = writeLocal(mid, l);
                dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

                id = stmtMap.getNextIndex();
                argIdList.add(vid);
            }
            else
                throw new RuntimeException("Unsupported actual argument: " + v);
        }
        for (int i = 0; i < argIdList.size(); ++i)
            dbWriter.writeArgument(argIdList.get(i), i, id);

        // Write 'this' argument
        Integer baseId = null;
        if (expr instanceof InstanceInvokeExpr)
        {
            InstanceInvokeExpr iExpr = (InstanceInvokeExpr) expr;
            Local base = (Local) iExpr.getBase();
            baseId = varMap.getIndex(new LocalVariable(base));
        }

        InvokeKind kind;
        if (expr instanceof StaticInvokeExpr)
            kind = InvokeKind.STATIC;
        else if (expr instanceof VirtualInvokeExpr)
            kind = InvokeKind.VIRTUAL;
        else if (expr instanceof InterfaceInvokeExpr)
            kind = InvokeKind.INTERFACE;
        else if (expr instanceof SpecialInvokeExpr)
            kind = InvokeKind.SPECIAL;
        else
            throw new RuntimeException("Unsupported invoke expr: " + expr);

        SootMethod targetMethod = expr.getMethod();
        if (!methodMap.exist(targetMethod)) {
            System.err.println("[JimpleDumper] Warning: cannot find the implementation of method  " + targetMethod);
            writeMethodDecl(targetMethod);
        }
        int targetId = methodMap.getIndexOrFail(targetMethod);

        dbWriter.writeInvokeInstruction(id, kind, targetId, baseId, lineno, retId, mid);
    }

    private void writeInvokeStmt(int mid, InvokeStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        InvokeExpr expr = stmt.getInvokeExpr();
        LineNumberTag tag = (LineNumberTag) stmt.getTag("LineNumberTag");
        writeInvokeExpr(id, expr, mid, tag == null ? null : tag.getLineNumber(), null);
    }

    private void writeReturnStmt(int mid, ReturnStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value v = stmt.getOp();

        if(v instanceof Local)
        {
            Local local = (Local) v;
            int vid = varMap.getIndexOrFail(new LocalVariable(local));
            dbWriter.writeReturnInstruction(id, vid, mid);
        }
        else if (v instanceof Constant)
        {
            Constant c = (Constant) v;
            int cid = getConstantId(c);

            // Fabricate an ASSIGN_CONST instruction here
            Local l = new JimpleLocal("const" + id, c.getType());
            int vid = writeLocal(mid, l);
            dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

            id = stmtMap.getNextIndex();
            dbWriter.writeReturnInstruction(id, vid, mid);
        }
        else
        {
            throw new RuntimeException("Unsupported throw stmt: " + stmt);
        }
    }

    private void writeReturnVoidStmt(int mid, ReturnVoidStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        dbWriter.writeReturnVoidInstruction(id, mid);
    }

    private void writeThrowStmt(int mid, ThrowStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value v = stmt.getOp();

        if(v instanceof Local)
        {
            Local local = (Local) v;
            int vid = varMap.getIndexOrFail(new LocalVariable(local));
            dbWriter.writeThrowInstruction(id, vid, mid);
        }
        else
        {
            throw new RuntimeException("Unsupported throw stmt: " + stmt);
        }
    }

    private void writeEnterMonitorStmt(int mid, EnterMonitorStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value v = stmt.getOp();

        if(v instanceof Local)
        {
            Local local = (Local) v;
            int vid = varMap.getIndexOrFail(new LocalVariable(local));
            dbWriter.writeEnterMonitorInstruction(id, vid, mid);
        }
        else if (v instanceof Constant)
        {
            // Fabricate an ASSIGN_CONST instruction here
            Constant c = (Constant) v;
            int cid = getConstantId(c);
            Local l = new JimpleLocal("const" + id, c.getType());
            int vid = writeLocal(mid, l);
            dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

            id = stmtMap.getNextIndex();
            dbWriter.writeEnterMonitorInstruction(id, vid, mid);
        }
        else
        {
            throw new RuntimeException("Unsupported entermonitor stmt: " + stmt);
        }
    }

    private void writeExitMonitorStmt(int mid, ExitMonitorStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        Value v = stmt.getOp();

        if(v instanceof Local)
        {
            Local local = (Local) v;
            int vid = varMap.getIndexOrFail(new LocalVariable(local));
            dbWriter.writeExitMonitorInstruction(id, vid, mid);
        }
        else if (v instanceof Constant)
        {
            // Fabricate an ASSIGN_CONST instruction here
            Constant c = (Constant) v;
            int cid = getConstantId(c);
            Local l = new JimpleLocal("const" + id, c.getType());
            int vid = writeLocal(mid, l);
            dbWriter.writeAssignConstInstruction(id, vid, cid, mid);

            id = stmtMap.getNextIndex();
            dbWriter.writeExitMonitorInstruction(id, vid, mid);
        }
        else
        {
            throw new RuntimeException("Unsupported exitmonitor stmt: " + stmt);
        }
    }

    private void writeGotoStmt(int mid, GotoStmt stmt) throws SQLException
    {
        int id = stmtMap.getIndexOrFail(stmt);
        int target = stmtMap.getIndexOrFail((Stmt) stmt.getTarget());
        dbWriter.writeGotoInstruction(id, target, mid);
    }

    private void writeIfStmt(int mid, IfStmt stmt) throws SQLException
    {
        ConditionExpr expr = (ConditionExpr) stmt.getCondition();
        Stmt targetStmt = stmt.getTarget();

        int targetId = stmtMap.getIndexOrFail(targetStmt);
        IfInfo info = ifMap.get(stmt);
        dbWriter.writeIfInstruction(info.stmtId, info.conditionId, targetId, mid);
    }

    private void writeTableSwitchStmt(int mid, TableSwitchStmt stmt) throws SQLException
    {
        int id = tableSwitchMap.get(stmt);
        int indexCounter = 0;
        for (int tgtIndex = stmt.getLowIndex(); tgtIndex <= stmt.getHighIndex(); ++tgtIndex)
        {
            int targetId = stmtMap.getIndexOrFail((Stmt) stmt.getTarget(indexCounter));
            dbWriter.writeSwitchTarget(id, tgtIndex, targetId);
            ++indexCounter;
        }

        int defaultId = stmtMap.getIndexOrFail((Stmt) stmt.getDefaultTarget());
        dbWriter.writeDefaultSwitchTarget(id, defaultId);
    }
    private void writeLookupSwitchStmt(int mid, LookupSwitchStmt stmt) throws SQLException
    {
        int id = lookupSwitchMap.get(stmt);
        for (int i = 0; i < stmt.getTargetCount(); ++i)
        {
            int tgtIndex = stmt.getLookupValue(i);
            int targetId = stmtMap.getIndexOrFail((Stmt) stmt.getTarget(i));
            dbWriter.writeSwitchTarget(id, tgtIndex, targetId);
        }

        int defaultId = stmtMap.getIndexOrFail((Stmt) stmt.getDefaultTarget());
        dbWriter.writeDefaultSwitchTarget(id, defaultId);
    }

    private void writeNopStmt(int mid, Stmt stmt) throws SQLException
    {
        int id = stmtMap.getIndex(stmt);
        dbWriter.writeNopInstruction(id, mid);
    }

    private void writeTraps(int mid, Body body) throws SQLException
    {
        for (Trap trap: body.getTraps())
        {
            int trapid = trapMap.getIndex(trap);
            assert(!trap.getException().isInterface());
            int typeid = typeMap.getIndexOrFail(trap.getException().getType());
            int startid = stmtMap.getIndexOrFail((Stmt) trap.getBeginUnit());
            int endid = stmtMap.getIndexOrFail((Stmt) trap.getEndUnit());

            IdentityStmt idStmt = (IdentityStmt) trap.getHandlerUnit();
            int hid = stmtMap.getIndexOrFail(idStmt);

            Local lhs = (Local) idStmt.getLeftOp();
            if (!(idStmt.getRightOp() instanceof CaughtExceptionRef))
                throw new RuntimeException("Trap not supported: " + idStmt.getRightOp());
            int vid = varMap.getIndex(new LocalVariable(lhs));

            dbWriter.writeExceptionHandler(trapid, mid, typeid, vid, startid, endid, hid + 1);
        }
    }

    private void writeStmts(int mid, Body body) throws SQLException
    {
        for (Unit unit: body.getUnits())
        {
            Stmt stmt = (Stmt) unit;
            if(stmt instanceof AssignStmt)
            {
                writeAssignStmt(mid, (AssignStmt) stmt);
            }
            else if(stmt instanceof IdentityStmt)
            {
                writeIdentityStmt(mid, body.getMethod(), (IdentityStmt) stmt);
            }
            else if(stmt instanceof InvokeStmt)
            {
                writeInvokeStmt(mid, (InvokeStmt) stmt);
            }
            else if(stmt instanceof ReturnStmt)
            {
                writeReturnStmt(mid, (ReturnStmt) stmt);
            }
            else if(stmt instanceof ReturnVoidStmt)
            {
                writeReturnVoidStmt(mid, (ReturnVoidStmt)stmt);
            }
            else if(stmt instanceof ThrowStmt)
            {
                writeThrowStmt(mid, (ThrowStmt) stmt);
            }
            else if(stmt instanceof EnterMonitorStmt)
            {
                writeEnterMonitorStmt(mid, (EnterMonitorStmt) stmt);
            }
            else if(stmt instanceof ExitMonitorStmt)
            {
                writeExitMonitorStmt(mid, (ExitMonitorStmt) stmt);
            }
            else if(stmt instanceof GotoStmt)
            {
                stmtMap.getIndex(stmt);
            }
            else if(stmt instanceof IfStmt)
            {
                // We need to prepare for the if condition first
                int id = stmtMap.getIndex(stmt);

                IfStmt ifStmt = (IfStmt) stmt;
                ConditionExpr expr = (ConditionExpr) ifStmt.getCondition();
                BinOpKind kind = BinOpKind.getOpKind(expr);
                Value op0 = expr.getOp1();
                int op0Id;
                if (op0 instanceof Local)
                {
                    Local local = (Local) op0;
                    op0Id = varMap.getIndex(new LocalVariable(local));
                }
                else if (op0 instanceof Constant)
                {
                    // Fabricate an ASSIGN_CONST instruction here
                    Constant c = (Constant) op0;
                    int cid = getConstantId(c);
                    Local l = new JimpleLocal("const" + id, c.getType());
                    op0Id = writeLocal(mid, l);
                    dbWriter.writeAssignConstInstruction(id, op0Id, cid, mid);

                    id = stmtMap.getNextIndex();
                }
                else
                    throw new RuntimeException("Unsupported operand: " + op0);

                Value op1 = expr.getOp2();
                int op1Id;
                if (op1 instanceof Local)
                {
                    Local local = (Local) op1;
                    op1Id = varMap.getIndex(new LocalVariable(local));
                }
                else if (op1 instanceof Constant)
                {
                    // Fabricate an ASSIGN_CONST instruction here
                    Constant c = (Constant) op1;
                    int cid = getConstantId(c);
                    Local l = new JimpleLocal("const" + id, c.getType());
                    op1Id = writeLocal(mid, l);
                    dbWriter.writeAssignConstInstruction(id, op1Id, cid, mid);

                    id = stmtMap.getNextIndex();
                }
                else
                    throw new RuntimeException("Unsupported operand: " + op1);

                // Fabricate a BINARY_OPERATION instruction here
                Local condLocal = new JimpleLocal("if_cond" + id, expr.getType());
                int condId = writeLocal(mid, condLocal);
                dbWriter.writeBinaryOpInstruction(id, kind, condId, op0Id, op1Id, mid);
                id = stmtMap.getNextIndex();

                ifMap.put(ifStmt, new IfInfo(id, condId));
            }
            else if(stmt instanceof TableSwitchStmt)
            {
                int id = stmtMap.getIndex(stmt);
                TableSwitchStmt switchStmt = (TableSwitchStmt) stmt;
                Value key = switchStmt.getKey();

                int keyId;
                if (key instanceof Local)
                {
                    Local local = (Local) key;
                    keyId = varMap.getIndex(new LocalVariable(local));
                }
                else if (key instanceof Constant)
                {
                    // Fabricate an ASSIGN_CONST instruction here
                    Constant c = (Constant) key;
                    int cid = getConstantId(c);
                    Local l = new JimpleLocal("const" + id, c.getType());
                    keyId = writeLocal(mid, l);
                    dbWriter.writeAssignConstInstruction(id, keyId, cid, mid);

                    id = stmtMap.getNextIndex();
                }
                else
                    throw new RuntimeException("Unsupported switch key: " + key);

                dbWriter.writeTableSwitchInstruction(id, keyId, mid);
                tableSwitchMap.put(switchStmt, id);
            }
            else if(stmt instanceof LookupSwitchStmt)
            {
                int id = stmtMap.getIndex(stmt);
                LookupSwitchStmt switchStmt = (LookupSwitchStmt) stmt;
                Value key = switchStmt.getKey();

                int keyId;
                if (key instanceof Local)
                {
                    Local local = (Local) key;
                    keyId = varMap.getIndex(new LocalVariable(local));
                }
                else if (key instanceof Constant)
                {
                    // Fabricate an ASSIGN_CONST instruction here
                    Constant c = (Constant) key;
                    int cid = getConstantId(c);
                    Local l = new JimpleLocal("const" + id, c.getType());
                    keyId = writeLocal(mid, l);
                    dbWriter.writeAssignConstInstruction(id, keyId, cid, mid);

                    id = stmtMap.getNextIndex();
                }
                else
                    throw new RuntimeException("Unsupported switch key: " + key);

                dbWriter.writeLookupSwitchInstruction(id, keyId, mid);
                lookupSwitchMap.put(switchStmt, id);
            }
            else
            {
                writeNopStmt(mid, stmt);
            }
        }
    }

    private void writeJumpStmts(int mid, Body body) throws SQLException
    {
        for (Unit unit: body.getUnits())
        {
            Stmt stmt = (Stmt) unit;
            if(stmt instanceof GotoStmt)
            {
                writeGotoStmt(mid, (GotoStmt) stmt);
            }
            else if(stmt instanceof IfStmt)
            {
                writeIfStmt(mid, (IfStmt) stmt);
            }
            else if(stmt instanceof TableSwitchStmt)
            {
                writeTableSwitchStmt(mid, (TableSwitchStmt) stmt);
            }
            else if(stmt instanceof LookupSwitchStmt)
            {
                writeLookupSwitchStmt(mid, (LookupSwitchStmt) stmt);
            }
        }
    }

    private void writeBody(int mid, Body body) throws SQLException
    {
        writeLocals(mid, body);
        writeStmts(mid, body);
        writeJumpStmts(mid, body);
        writeTraps(mid, body);
    }

    private void writeMethodDecl(SootMethod method) throws SQLException {
        int mid = methodMap.getIndex(method);
        String name = method.getName();
        String sig = method.getSignature();
        int parentId = getTypeId(method.getDeclaringClass().getType());
        int modifier = method.getModifiers();
        dbWriter.writeMethod(mid, name, sig, parentId, modifier);

        if (!(method.getReturnType() instanceof VoidType))
        {
            int tid = getTypeId(method.getReturnType());
            dbWriter.writeMethodReturnType(mid, tid);
        }

        if (!method.isStatic())
        {
            SootVariable var = new ThisVariable(method);
            int vid = varMap.getIndex(var);
            dbWriter.writeVariable(vid, var.getName(), parentId, mid);
            dbWriter.writeMethodThisParam(mid, vid);
        }

        for(int i = 0 ; i < method.getParameterCount(); i++)
        {
            SootVariable var = new ParamVariable(method, i);
            int vid = varMap.getIndex(var);
            int tid = getTypeId(method.getParameterType(i));
            dbWriter.writeMethodParam(mid, i, vid);
            dbWriter.writeVariable(vid, var.getName(), tid, mid);
        }

        for (SootClass exceptClass: method.getExceptions())
        {
            assert(!exceptClass.isInterface());
            int eid = typeMap.getIndexOrFail(exceptClass.getType());
            dbWriter.writeExceptionDeclaration(mid, eid);
        }
    }

    private void writeMethodDecls(SootClass cl) throws SQLException {
        for (SootMethod method: cl.getMethods())
            writeMethodDecl(method);
    }

    private void writeMethodBodys(SootClass cl, boolean ssa) throws SQLException
    {
        ArrayList<SootMethod> methods = new ArrayList<>();
        methods.addAll(cl.getMethods());
        for (SootMethod method: methods)
        {
            int mid = methodMap.getIndexOrFail(method);
            if (!method.isAbstract() && !method.isNative() && !method.isPhantom())
            {
                // This is an EXTREMELY slow operation
                if (!method.hasActiveBody())
                    method.retrieveActiveBody();

                // Transform to SSA
                Body body = method.getActiveBody();
                if (ssa) {
                    body = Shimple.v().newBody(body);
                    method.setActiveBody(body);
                }

                writeBody(mid, body);
                method.releaseActiveBody();
            }
        }
    }

    private void writeClass(SootClass cl) throws SQLException
    {
        String name = cl.getName();
        int id = typeMap.getIndex(cl.getType());

        if (cl.isInterface())
        {
            dbWriter.writeInterfaceType(id, name);
        }
        else
        {
            dbWriter.writeClassType(id, name);
            if (cl.hasSuperclass())
            {
                int sid = typeMap.getIndex(cl.getSuperclass().getType());
                dbWriter.writeSuperclass(id, sid);
            }
        }

        for (SootClass iface: cl.getInterfaces())
        {
            int sid = typeMap.getIndex(iface.getType());
            dbWriter.writeSuperinterface(id, sid);
        }
    }

    private void writeClasses(List<SootClass> classes, boolean ssa) throws SQLException
    {
        writeNullType();
        writePrimitiveTypes();
        for (SootClass cl: classes)
            writeClass(cl);
        for (SootClass cl: classes)
            writeFields(cl);
        for (SootClass cl: classes) {
            writeMethodDecls(cl);
        }
        for (SootClass cl: classes) {
            writeMethodBodys(cl, ssa);
        }
    }

    public static void writeJimple(String outfile, List<SootClass> classes, boolean ssa)
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + outfile);
            // We really don't care about transaction semantics: if something went wrong, just restart the entire process.
            connection.setAutoCommit(false);

            JimpleWriter writer = new JimpleWriter(connection);
            System.out.println("[JimpleDumper] Database connection established");

            long startTime = System.currentTimeMillis();
            writer.writeClasses(classes, ssa);
            connection.commit();

            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("[JimpleDumper] Database transaction takes " + elapsedTime + " seconds to finish");
            System.out.println("[JimpleDumper] Program serialization succeeded");
        } catch (SQLException e)
        {
            System.err.println("[JimpleDumper] Jimple serialization failed: " + e.getMessage());
        } finally
        {
            try
            {
                if (connection != null)
                    connection.close();
            } catch (SQLException e)
            {
                System.err.println("[JimpleDumper] Jimple serialization failed: " + e.getMessage());
            }
        }
    }
}
