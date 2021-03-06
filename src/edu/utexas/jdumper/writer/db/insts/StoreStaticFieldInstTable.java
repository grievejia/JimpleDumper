package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StoreStaticFieldInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "storestaticfieldinsts";

    public StoreStaticFieldInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "field INTEGER NOT NULL, " +
                                    "rhs INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?)");
    }

    public void insert(int id, int field, int rhs, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, field);
        insertStmt.setInt(3, rhs);
        insertStmt.setInt(4, mid);
        insertStmt.executeUpdate();
    }
}
