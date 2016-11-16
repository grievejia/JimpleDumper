package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmptyArrayTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "emptyarray";

    public EmptyArrayTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?)");
    }

    public void insert(int id) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.executeUpdate();
    }
}
