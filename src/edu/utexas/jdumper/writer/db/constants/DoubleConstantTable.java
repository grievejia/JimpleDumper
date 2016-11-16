package edu.utexas.jdumper.writer.db.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleConstantTable {
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "doubleconstants";

    public DoubleConstantTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        // SQLite3 treats NaN as NULL, so we can't have a NOT-NULL value column
        connection.prepareStatement("CREATE TABLE " +
                TABLE_NAME +
                " (" +
                "id INTEGER PRIMARY KEY, " +
                "value REAL" +
                ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                TABLE_NAME +
                " VALUES(?, ?)");
    }

    public void insert(int id, double value) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setDouble(2, value);
        insertStmt.executeUpdate();
    }
}
