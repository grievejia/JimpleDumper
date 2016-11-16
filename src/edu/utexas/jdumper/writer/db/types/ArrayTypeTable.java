package edu.utexas.jdumper.writer.db.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArrayTypeTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "arraytypes";

    public ArrayTypeTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "elemid INTEGER NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int id, int elemId) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, elemId);
        insertStmt.executeUpdate();
    }
}
