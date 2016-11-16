package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoadStaticFieldInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "loadstaticfieldinsts";

    public LoadStaticFieldInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "lhs INTEGER NOT NULL, " +
                                    "field INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?)");
    }

    public void insert(int id, int lhs, int field, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, lhs);
        insertStmt.setInt(3, field);
        insertStmt.setInt(4, mid);
        insertStmt.executeUpdate();
    }
}
