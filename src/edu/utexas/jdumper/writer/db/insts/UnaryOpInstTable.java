package edu.utexas.jdumper.writer.db.insts;

import edu.utexas.jdumper.soot.UnOpKind;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnaryOpInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "unaryopinsts";

    public UnaryOpInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "kind INTEGER NOT NULL, " +
                                    "lhs INTEGER NOT NULL, " +
                                    "rhs INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?)");
    }

    public void insert(int id, UnOpKind kind, int lhs, int rhs, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, kind.getKindId());
        insertStmt.setInt(3, lhs);
        insertStmt.setInt(4, rhs);
        insertStmt.setInt(5, mid);
        insertStmt.executeUpdate();
    }
}
