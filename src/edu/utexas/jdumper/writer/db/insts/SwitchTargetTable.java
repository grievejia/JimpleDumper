package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SwitchTargetTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "switchtargets";

    public SwitchTargetTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "inst INTEGER NOT NULL, " +
                                    "idx INTEGER NOT NULL, " +
                                    "target INTEGER NOT NULL, " +
                                    "PRIMARY KEY (inst, idx) " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int inst, int index, int target) throws SQLException
    {
        insertStmt.setInt(1, inst);
        insertStmt.setInt(2, index);
        insertStmt.setInt(3, target);
        insertStmt.executeUpdate();
    }
}
