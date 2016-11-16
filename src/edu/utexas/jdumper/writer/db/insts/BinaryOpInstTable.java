package edu.utexas.jdumper.writer.db.insts;

import edu.utexas.jdumper.soot.BinOpKind;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BinaryOpInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "binaryopinsts";

    public BinaryOpInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "kind INTEGER NOT NULL, " +
                                    "lhs INTEGER NOT NULL, " +
                                    "op0 INTEGER NOT NULL, " +
                                    "op1 INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?, ?)");
    }

    public void insert(int id, BinOpKind kind, int lhs, int op0, int op1, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, kind.getKindId());
        insertStmt.setInt(3, lhs);
        insertStmt.setInt(4, op0);
        insertStmt.setInt(5, op1);
        insertStmt.setInt(6, mid);
        insertStmt.executeUpdate();
    }
}
