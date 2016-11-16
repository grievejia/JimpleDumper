package edu.utexas.jdumper.writer;

import edu.utexas.jdumper.soot.*;
import edu.utexas.jdumper.writer.db.*;
import edu.utexas.jdumper.writer.db.insts.*;
import edu.utexas.jdumper.writer.db.types.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class DatabaseWriter
{
    private Connection connection;

    TypeTable typeTable;
    PrimitiveTypeTable primitiveTypeTable;
    ClassTypeTable classTypeTable;
    InterfaceTypeTable interfaceTypeTable;
    ArrayTypeTable arrayTypeTable;
    SuperClassTable superClassTable;
    SuperInterfaceTable superInterfaceTable;

    FieldTable fieldTable;
    MethodTable methodTable;
    MethodParamTable methodParamTable;
    MethodReturnTable methodReturnTable;
    ExceptionDeclTable exceptionDeclTable;
    VariableTable variableTable;
    ConstantTable constantTable;
    ExceptionHandlerTable exceptionHandlerTable;

    InstructionTable instructionTable;
    NoopInstTable noopInstTable;
    ThrowInstTable throwInstTable;
    EnterMonitorInstTable enterMonitorInstTable;
    ExitMonitorInstTable exitMonitorInstTable;
    ReturnInstTable returnInstTable;
    AllocSiteTable allocSiteTable;
    EmptyArrayTable emptyArrayTable;
    AssignAllocInstTable assignAllocInstTable;
    AssignConstInstTable assignConstInstTable;
    AssignVariableInstTable assignVariableInstTable;
    AssignCastInstTable assignCastInstTable;
    AssignPhiInstTable assignPhiInstTable;
    PhiTargetTable phiTargetTable;
    InvokeInstTable invokeInstTable;
    ArgumentTable argumentTable;
    LoadInstanceFieldInstTable loadInstanceFieldInstTable;
    LoadStaticFieldInstTable loadStaticFieldInstTable;
    LoadArrayInstTable loadArrayInstTable;
    StoreInstanceFieldInstTable storeInstanceFieldInstTable;
    StoreStaticFieldInstTable storeStaticFieldInstTable;
    StoreArrayInstTable storeArrayInstTable;
    UnaryOpInstTable unaryOpInstTable;
    BinaryOpInstTable binaryOpInstTable;
    GotoInstTable gotoInstTable;
    IfInstTable ifInstTable;
    LookupSwitchTable lookupSwitchTable;
    TableSwitchTable tableSwitchTable;
    SwitchTargetTable switchTargetTable;
    DefaultSwitchTargetTable defaultSwitchTargetTable;

    DatabaseWriter(Connection connection) throws SQLException
    {
        this.connection = connection;

        typeTable = new TypeTable(connection);
        primitiveTypeTable = new PrimitiveTypeTable(connection);
        classTypeTable = new ClassTypeTable(connection);
        interfaceTypeTable = new InterfaceTypeTable(connection);
        arrayTypeTable = new ArrayTypeTable(connection);
        superClassTable = new SuperClassTable(connection);
        superInterfaceTable = new SuperInterfaceTable(connection);

        fieldTable = new FieldTable(connection);
        methodTable = new MethodTable(connection);
        methodParamTable = new MethodParamTable(connection);
        methodReturnTable = new MethodReturnTable(connection);
        exceptionDeclTable = new ExceptionDeclTable(connection);
        variableTable = new VariableTable(connection);
        constantTable = new ConstantTable(connection);
        instructionTable = new InstructionTable(connection);
        exceptionHandlerTable = new ExceptionHandlerTable(connection);

        noopInstTable = new NoopInstTable(connection);
        throwInstTable = new ThrowInstTable(connection);
        enterMonitorInstTable = new EnterMonitorInstTable(connection);
        exitMonitorInstTable = new ExitMonitorInstTable(connection);
        returnInstTable = new ReturnInstTable(connection);
        allocSiteTable = new AllocSiteTable(connection);
        emptyArrayTable = new EmptyArrayTable(connection);
        assignAllocInstTable = new AssignAllocInstTable(connection);
        assignConstInstTable = new AssignConstInstTable(connection);
        assignVariableInstTable = new AssignVariableInstTable(connection);
        assignCastInstTable = new AssignCastInstTable(connection);
        assignPhiInstTable = new AssignPhiInstTable(connection);
        phiTargetTable = new PhiTargetTable(connection);
        invokeInstTable = new InvokeInstTable(connection);
        argumentTable = new ArgumentTable(connection);
        loadInstanceFieldInstTable = new LoadInstanceFieldInstTable(connection);
        loadStaticFieldInstTable = new LoadStaticFieldInstTable(connection);
        loadArrayInstTable = new LoadArrayInstTable(connection);
        storeInstanceFieldInstTable = new StoreInstanceFieldInstTable(connection);
        storeStaticFieldInstTable = new StoreStaticFieldInstTable(connection);
        storeArrayInstTable = new StoreArrayInstTable(connection);
        unaryOpInstTable = new UnaryOpInstTable(connection);
        binaryOpInstTable = new BinaryOpInstTable(connection);
        gotoInstTable = new GotoInstTable(connection);
        ifInstTable = new IfInstTable(connection);
        lookupSwitchTable = new LookupSwitchTable(connection);
        tableSwitchTable = new TableSwitchTable(connection);
        switchTargetTable = new SwitchTargetTable(connection);
        defaultSwitchTargetTable = new DefaultSwitchTargetTable(connection);
    }

    void writeNullType(int id) throws SQLException
    {
        typeTable.insert(id, TypeKind.NULL);
    }

    void writePrimitiveType(int id, String name) throws SQLException
    {
        typeTable.insert(id, TypeKind.PRIMITIVE);
        primitiveTypeTable.insert(id, name);
    }

    void writeArrayType(int id, int elemId) throws SQLException
    {
        typeTable.insert(id, TypeKind.ARRAY);
        arrayTypeTable.insert(id, elemId);
    }

    void writeClassType(int id, String name) throws SQLException
    {
        typeTable.insert(id, TypeKind.CLASS);
        classTypeTable.insert(id, name);
    }

    void writeInterfaceType(int id, String name) throws SQLException
    {
        typeTable.insert(id, TypeKind.INTERFACE);
        interfaceTypeTable.insert(id, name);
    }

    void writeSuperclass(int id, int sid) throws SQLException
    {
        superClassTable.insert(id, sid);
    }

    void writeSuperinterface(int id, int sid) throws SQLException
    {
        superInterfaceTable.insert(id, sid);
    }

    void writeField(int id, String name, int tid, int pid, int modifier) throws SQLException
    {
        fieldTable.insert(id, name, tid, pid, modifier);
    }

    void writeMethod(int mid, String name, String sig, int pid, int modifier) throws SQLException
    {
        methodTable.insert(mid, name, sig, pid, modifier);
    }

    void writeMethodParamType(int mid, int index, int tid) throws SQLException
    {
        methodParamTable.insert(mid, index, tid);
    }

    void writeMethodReturnType(int mid, int tid) throws SQLException
    {
        methodReturnTable.insert(mid, tid);
    }

    void writeConstant(int cid, ConstantKind kind) throws SQLException
    {
        constantTable.insert(cid, kind);
    }

    void writeVariable(int vid, String name, int tid, int mid) throws SQLException
    {
        variableTable.insert(vid, name, tid, mid);
    }

    void writeExceptionDeclaration(int mid, int eid) throws SQLException
    {
       exceptionDeclTable.insert(mid, eid);
    }

    void writeExceptionHandler(int hid, int tid, int pid, int sid, int eid, int did) throws SQLException
    {
       exceptionHandlerTable.insert(hid, tid, pid, sid, eid, did);
    }

    void writeNopInstruction(int id, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.NOP);
        noopInstTable.insert(id, mid);
    }

    void writeThrowInstruction(int id, int vid, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.THROW);
        throwInstTable.insert(id, vid, mid);
    }

    void writeEnterMonitorInstruction(int id, int vid, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ENTERMONITOR);
        enterMonitorInstTable.insert(id, vid, mid);
    }

    void writeExitMonitorInstruction(int id, int vid, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ENTERMONITOR);
        exitMonitorInstTable.insert(id, vid, mid);
    }

    void writeReturnInstruction(int id, int vid, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.RETURN);
        returnInstTable.insert(id, vid, mid);
    }

    void writeReturnVoidInstruction(int id, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.RETURN);
        returnInstTable.insert(id, mid);
    }

    void writeAssignConstInstruction(int id, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ASSIGN_CONST);
        assignConstInstTable.insert(id, lhs, rhs, mid);
    }

    void writeAssignVariableInstruction(int id, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ASSIGN_VAR);
        assignVariableInstTable.insert(id, lhs, rhs, mid);
    }

    void writeAssignPhiInstruction(int id, int lhs, List<Integer> rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ASSIGN_PHI);
        assignPhiInstTable.insert(id, lhs, mid);
        for (Integer i: rhs)
            phiTargetTable.insert(id, i);
    }

    void writeAssignCastInstruction(int id, int lhs, int rhs, int tid, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ASSIGN_CAST);
        assignCastInstTable.insert(id, lhs, rhs, tid, mid);
    }

    void writeInvokeInstruction(int id, InvokeKind kind, int target, Integer base, Integer lineno, Integer ret, int mid) throws SQLException
    {
        instructionTable.insert(id, kind.getInstructionKind());
        invokeInstTable.insert(id, kind, target, base, lineno, ret, mid);
    }

    void writeArgument(int argId, int index, int instId) throws SQLException
    {
        argumentTable.insert(argId, index, instId);
    }

    void writeAllocSite(int id, int tid, int mid) throws SQLException
    {
        allocSiteTable.insert(id, tid, mid);
    }

    void writeEmptyArray(int id) throws SQLException
    {
        emptyArrayTable.insert(id);
    }

    void writeAssignAllocInstruction(int id, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.ASSIGN_ALLOC);
        assignAllocInstTable.insert(id, lhs, rhs, mid);
    }

    void writeLoadInstanceFieldInstruction(int id, int lhs, int rhs, int field, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.LOAD_INSTANCE);
        loadInstanceFieldInstTable.insert(id, lhs, rhs, field, mid);
    }

    void writeStoreInstanceFieldInstruction(int id, int lhs, int field, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.STORE_INSTANCE);
        storeInstanceFieldInstTable.insert(id, lhs, field, rhs, mid);
    }

    void writeLoadStaticFieldInstruction(int id, int lhs, int field, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.LOAD_STATIC);
        loadStaticFieldInstTable.insert(id, lhs, field, mid);
    }

    void writeStoreStaticFieldInstruction(int id, int field, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.STORE_STATIC);
        storeStaticFieldInstTable.insert(id, field, rhs, mid);
    }

    void writeLoadArrayInstruction(int id, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.LOAD_ARRAY);
        loadArrayInstTable.insert(id, lhs, rhs, mid);
    }

    void writeStoreArrayInstruction(int id, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.STORE_ARRAY);
        storeArrayInstTable.insert(id, lhs, rhs, mid);
    }

    void writeUnaryOpInstruction(int id, UnOpKind kind, int lhs, int rhs, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.UNARY_OPERATION);
        unaryOpInstTable.insert(id, kind, lhs, rhs, mid);
    }

    void writeBinaryOpInstruction(int id, BinOpKind kind, int lhs, int op0, int op1, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.BINARY_OPERATION);
        binaryOpInstTable.insert(id, kind, lhs, op0, op1, mid);
    }

    void writeGotoInstruction(int id, int target, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.GOTO);
        gotoInstTable.insert(id, target, mid);
    }

    void writeIfInstruction(int id, int cond, int target, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.IF);
        ifInstTable.insert(id, cond, target, mid);
    }

    void writeLookupSwitchInstruction(int id, int key, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.LOOKUPSWITCH);
        lookupSwitchTable.insert(id, key, mid);
    }

    void writeTableSwitchInstruction(int id, int key, int mid) throws SQLException
    {
        instructionTable.insert(id, InstructionKind.TABLESWITCH);
        tableSwitchTable.insert(id, key, mid);
    }

    void writeSwitchTarget(int inst, int index, int target) throws SQLException
    {
        switchTargetTable.insert(inst, index, target);
    }

    void writeDefaultSwitchTarget(int inst, int target) throws SQLException
    {
        defaultSwitchTargetTable.insert(inst, target);
    }
}