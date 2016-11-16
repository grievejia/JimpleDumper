package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MethodTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "methods";

    public MethodTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "name TEXT NOT NULL, " +
                                                 "sig TEXT NOT NULL, " +
                                                 "parent INTEGER NOT NULL, " +
                                                 "modifier INTEGER NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?)");
    }

    public void insert(int id, String name, String sig, int pid, int modifier) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setString(2, name);
        insertStmt.setString(3, sig);
        insertStmt.setInt(4, pid);
        insertStmt.setInt(5, modifier);
        insertStmt.executeUpdate();
    }
}
