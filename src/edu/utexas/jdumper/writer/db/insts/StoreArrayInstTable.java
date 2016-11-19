package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StoreArrayInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "storearrayinsts";

    public StoreArrayInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "lhs INTEGER NOT NULL, " +
                                    "arrayidx INTEGER NOT NULL, " +
                                    "rhs INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?)");
    }

    public void insert(int id, int lhs, int arrayIdx, int rhs, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, lhs);
        insertStmt.setInt(3, arrayIdx);
        insertStmt.setInt(4, rhs);
        insertStmt.setInt(5, mid);
        insertStmt.executeUpdate();
    }
}
