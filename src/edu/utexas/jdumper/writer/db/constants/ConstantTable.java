package edu.utexas.jdumper.writer.db.constants;

import edu.utexas.jdumper.soot.ConstantKind;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConstantTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "constants";

    public ConstantTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                    TABLE_NAME +
                                    " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "kind INTEGER NOT NULL " +
                                    ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int id, ConstantKind kind) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, kind.getKindId());
        insertStmt.executeUpdate();
    }
}
