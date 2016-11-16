package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExceptionDeclTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "exceptiondecls";

    public ExceptionDeclTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "method INTEGER NOT NULL, " +
                                                 "exception INTEGER NOT NULL, " +
                                                 "UNIQUE (method, exception)" +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?)");
    }

    public void insert(int mid, int eid) throws SQLException
    {
        insertStmt.setInt(1, mid);
        insertStmt.setInt(2, eid);
        insertStmt.executeUpdate();
    }
}
