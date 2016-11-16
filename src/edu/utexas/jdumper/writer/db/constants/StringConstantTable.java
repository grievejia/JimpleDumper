package edu.utexas.jdumper.writer.db.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringConstantTable {
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "stringconstants";

    public StringConstantTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                TABLE_NAME +
                " (" +
                "id INTEGER PRIMARY KEY, " +
                "value TEXT NOT NULL" +
                ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                TABLE_NAME +
                " VALUES(?, ?)");
    }

    public void insert(int id, String value) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setString(2, value);
        insertStmt.executeUpdate();
    }
}
