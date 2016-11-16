package edu.utexas.jdumper.writer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExceptionHandlerTable
{
    private PreparedStatement insertStmt;

    private static final String TABLE_NAME = "exceptionhandlers";

    public ExceptionHandlerTable(Connection connection) throws SQLException
    {
        connection.prepareStatement("DROP TABLE IF EXISTS " + TABLE_NAME).executeUpdate();
        connection.prepareStatement("CREATE TABLE " +
                                                 TABLE_NAME +
                                                 " (" +
                                                 "id INTEGER PRIMARY KEY, " +
                                                 "method INTEGER NOT NULL," +
                                                 "type INTEGER NOT NULL, " +
                                                 "param INTEGER NOT NULL, " +
                                                 "start INTEGER NOT NULL, " +
                                                 "end INTEGER NOT NULL, " +
                                                 "dst INTEGER NOT NULL " +
                                                 ")").executeUpdate();
        insertStmt = connection.prepareStatement("INSERT INTO " +
                                                 TABLE_NAME +
                                                 " VALUES(?, ?, ?, ?, ?, ?, ?)");
    }

    public void insert(int handerId, int methodId, int typeId, int paramId, int startId, int endId, int dstId) throws SQLException
    {
        insertStmt.setInt(1, handerId);
        insertStmt.setInt(2, methodId);
        insertStmt.setInt(3, typeId);
        insertStmt.setInt(4, paramId);
        insertStmt.setInt(5, startId);
        insertStmt.setInt(6, endId);
        insertStmt.setInt(7, dstId);
        insertStmt.executeUpdate();
    }
}
