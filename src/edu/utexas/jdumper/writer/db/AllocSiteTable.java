package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AllocSiteTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "allocsites";

    public AllocSiteTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "type INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL" +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int id, int tid, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, tid);
        insertStmt.setInt(3, mid);
        insertStmt.executeUpdate();
    }
}
