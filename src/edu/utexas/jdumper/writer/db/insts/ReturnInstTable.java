package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ReturnInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "returninsts";

    public ReturnInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "var INTEGER, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int id, int vid, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, vid);
        insertStmt.setInt(3, mid);
        insertStmt.executeUpdate();
    }

    public void insert(int id, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setNull(2, Types.INTEGER);
        insertStmt.setInt(3, mid);
        insertStmt.executeUpdate();
    }
}
