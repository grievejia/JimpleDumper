package edu.utexas.jdumper.writer.db.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntConstantTable {
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "intconstants";

    public IntConstantTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                TABLE_NAME +
                " (" +
                "id INTEGER PRIMARY KEY, " +
                "value INTEGER NOT NULL" +
                ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                TABLE_NAME +
                " VALUES(?, ?)");
    }

    public void insert(int id, int value) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, value);
        insertStmt.executeUpdate();
    }
}
