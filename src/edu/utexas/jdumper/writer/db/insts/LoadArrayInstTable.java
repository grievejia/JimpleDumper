package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoadArrayInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "loadarrayinsts";

    public LoadArrayInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "lhs INTEGER NOT NULL, " +
                                    "rhs INTEGER NOT NULL, " +
                                    "arrayidx INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?)");
    }

    public void insert(int id, int lhs, int rhs, int arrayIdx, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, lhs);
        insertStmt.setInt(3, rhs);
        insertStmt.setInt(4, arrayIdx);
        insertStmt.setInt(5, mid);
        insertStmt.executeUpdate();
    }
}
