package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FieldTable
{

    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "fields";

    public FieldTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "name TEXT NOT NULL, " +
                                                 "type INTEGER NOT NULL, " +
                                                 "parent INTEGER NOT NULL, " +
                                                 "modifier INTEGER NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?)");
    }

    public void insert(int fid, String name, int tid, int pid, int modifier) throws SQLException
    {
        insertStmt.setInt(1, fid);
        insertStmt.setString(2, name);
        insertStmt.setInt(3, tid);
        insertStmt.setInt(4, pid);
        insertStmt.setInt(5, modifier);
        insertStmt.executeUpdate();
    }
}
