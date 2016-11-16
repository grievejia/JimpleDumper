package edu.utexas.jdumper.writer.db.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuperInterfaceTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "superinterface";

    public SuperInterfaceTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER NOT NULL, " +
                                                 "superid INTEGER NOT NULL, " +
                                                 "PRIMARY KEY (id, superid)" +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int id, int superId) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, superId);
        insertStmt.executeUpdate();
    }
}
