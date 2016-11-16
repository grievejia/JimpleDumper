package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExitMonitorInstTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "exitmonitorinsts";

    public ExitMonitorInstTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "var INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int id, int vid, int mid) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, vid);
        insertStmt.setInt(3, mid);
        insertStmt.executeUpdate();
    }
}
