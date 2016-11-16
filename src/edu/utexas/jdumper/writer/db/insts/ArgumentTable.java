package edu.utexas.jdumper.writer.db.insts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArgumentTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "arguments";

    public ArgumentTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "inst INTEGER NOT NULL, " +
                                    "idx INTEGER NOT NULL, " +
                                    "var INTEGER NOT NULL, " +
                                    "PRIMARY KEY (inst, idx) " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?)");
    }

    public void insert(int id, int index, int inst) throws SQLException
    {
        insertStmt.setInt(1, inst);
        insertStmt.setInt(2, index);
        insertStmt.setInt(3, id);
        insertStmt.executeUpdate();
    }
}
