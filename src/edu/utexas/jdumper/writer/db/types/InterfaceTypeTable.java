package edu.utexas.jdumper.writer.db.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InterfaceTypeTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "interfacetypes";

    public InterfaceTypeTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "name TEXT NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int id, String name) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setString(2, name);
        insertStmt.executeUpdate();
    }
}
