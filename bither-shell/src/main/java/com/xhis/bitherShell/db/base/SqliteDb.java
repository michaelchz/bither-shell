package com.xhis.bitherShell.db.base;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.base.Function;

import net.bither.bitherj.db.AbstractDb;
import net.bither.bitherj.db.imp.base.ICursor;
import net.bither.bitherj.db.imp.base.IDb;

public class SqliteDb implements IDb {

    private Connection conn;

    private String dbFileFullName;
    protected String connectionString;

    public SqliteDb(String dbDir) {
        this.dbFileFullName = dbDir + File.separator + getDBName();
        this.connectionString = "jdbc:sqlite:" + dbFileFullName;
    }


    private String getDBName() {
    	return "bitherShell.db";
    }

    public boolean hasTable(String tableName) throws SQLException {
        ResultSet rs = conn.getMetaData().getTables(null, null, AbstractDb.Tables.TXS, null);
        boolean hasTable = rs.next();
        rs.close();
        return hasTable;
    }
 
    public void initDb() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            conn = DriverManager.getConnection(this.connectionString, null, null);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            File file = new File(dbFileFullName);
            if (file.exists()) {
                file.delete();
            }
			throw new RuntimeException(e);
        }
    }

	@Override
	public void beginTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
			throw new RuntimeException(e);
        }
	}

	@Override
	public void execUpdate(String sql, String[] params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setString(i + 1, params[i]);
                }
            }
            stmt.executeUpdate();
            conn.commit();
            stmt.close();
        } catch (SQLException e) {
			throw new RuntimeException(e);
        }
	}

	@Override
	public void execQueryOneRecord(String sql, String[] params,
			Function<ICursor, Void> func) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setString(i + 1, params[i]);
                }
            }

            ResultSet c = stmt.executeQuery();
            SqliteCursor cursor = new SqliteCursor(c);
            if (cursor.moveToNext()) {
            	func.apply(cursor);
            }
            cursor.close();
            stmt.close();
        } catch (SQLException e) {
			throw new RuntimeException(e);
        }
	}

	@Override
	public void execQueryLoop(String sql, String[] params,
			Function<ICursor, Void> func) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setString(i + 1, params[i]);
                }
            }

            ResultSet c = stmt.executeQuery();
            SqliteCursor cursor = new SqliteCursor(c);
            while (cursor.moveToNext()) {
            	func.apply(cursor);
            }
            cursor.close();
            stmt.close();
        } catch (SQLException e) {
			throw new RuntimeException(e);
        }
	}

}
