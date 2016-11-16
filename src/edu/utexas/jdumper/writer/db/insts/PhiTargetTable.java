package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhiTargetTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "phitargets";

    public PhiTargetTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "inst INTEGER NOT NULL, " +
                                    "var INTEGER NOT NULL, " +
                                    "PRIMARY KEY (inst, var) " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int inst, int var) throws SQLException
    {
        insertStmt.setInt(1, inst);
        insertStmt.setInt(2, var);
        insertStmt.executeUpdate();
    }
}
