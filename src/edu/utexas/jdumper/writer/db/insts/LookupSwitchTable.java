package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LookupSwitchTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "lookupswitchinsts";

    public LookupSwitchTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "inst INTEGER PRIMARY KEY, " +
                                    "key INTEGER NOT NULL, " +
                                    "method INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int inst, int key, int mid) throws SQLException
    {
        insertStmt.setInt(1, inst);
        insertStmt.setInt(2, key);
        insertStmt.setInt(3, mid);
        insertStmt.executeUpdate();
    }
}
