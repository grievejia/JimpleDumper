package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VariableTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "variables";

    public VariableTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "name TEXT NOT NULL, " +
                                                 "type INTEGER NOT NULL, " +
                                                 "parent INTEGER NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?)");
    }

    public void insert(int id, String name, int tid, int pid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setString(2, name);
        insertStmt.setInt(3, tid);
        insertStmt.setInt(4, pid);
        insertStmt.executeUpdate();
    }
}
