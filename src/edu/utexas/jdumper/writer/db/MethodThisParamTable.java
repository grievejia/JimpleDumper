package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MethodThisParamTable {
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "methodthisparam";

    public MethodThisParamTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                TABLE_NAME +
                " (" +
                "method INTEGER PRIMARY KEY, " +
                "vid INTEGER NOT NULL" +
                ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                TABLE_NAME +
                " VALUES(?, ?)");
    }

    public void insert(int mid, int vid) throws SQLException
    {
        insertStmt.setInt(1, mid);
        insertStmt.setInt(2, vid);
        insertStmt.executeUpdate();
    }
}
