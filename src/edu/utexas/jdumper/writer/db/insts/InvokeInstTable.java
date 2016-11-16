package edu.utexas.jdumper.writer.db.insts;

import edu.utexas.jdumper.soot.InvokeKind;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InvokeInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "invokeinsts";

    public InvokeInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "kind INTEGER NOT NULL, " +
                                    "target INTEGER NOT NULL, " +
                                    "base INTEGER, " +
                                    "lineno INTEGER," +
                                    "ret INTEGER, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?, ?, ?)");
    }

    public void insert(int id, InvokeKind kind, int target, Integer base, Integer lineno, Integer ret, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, kind.getKindId());
        insertStmt.setInt(3, target);
        if (base != null)
            insertStmt.setInt(4, base);
        else
            insertStmt.setNull(4, Types.INTEGER);
        if (lineno != null)
            insertStmt.setInt(5, lineno);
        else
            insertStmt.setNull(5, Types.INTEGER);
        if (ret != null)
            insertStmt.setInt(6, ret);
        else
            insertStmt.setNull(6, Types.INTEGER);
        insertStmt.setInt(7, mid);
        insertStmt.executeUpdate();
    }
}
