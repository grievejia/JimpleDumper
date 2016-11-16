package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MethodParamTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "methodparam";

    public MethodParamTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "method INTEGER NOT NULL, " +
                                    "idx INTEGER NOT NULL, " +
                                    "vid INTEGER NOT NULL, " +
                                    "PRIMARY KEY (method, idx)" +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int mid, int index, int vid) throws SQLException
    {
        insertStmt.setInt(1, mid);
        insertStmt.setInt(2, index);
        insertStmt.setInt(3, vid);
        insertStmt.executeUpdate();
    }
}
