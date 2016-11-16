package edu.utexas.jdumper.writer.db.types;

import edu.utexas.jdumper.soot.TypeKind;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TypeTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "types";

    public TypeTable(Connection connection) throws SQLException
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

    public void insert(int id, TypeKind kind) throws SQLException
    {
        insertStmt.setInt(1, id);
        insertStmt.setInt(2, kind.getKindId());
        insertStmt.executeUpdate();
    }
}
