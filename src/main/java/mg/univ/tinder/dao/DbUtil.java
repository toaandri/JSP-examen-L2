package mg.univ.tinder.dao;

import mg.univ.tinder.config.Db;

import java.sql.Connection;
import java.sql.SQLException;

public final class DbUtil {
    private DbUtil() {}

    public static Connection getConnection() throws SQLException {
        return Db.getDataSource().getConnection();
    }

    public static void rollbackQuietly(Connection c) {
        if (c == null) return;
        try { c.rollback(); } catch (SQLException ignored) {}
    }

    public static void closeQuietly(AutoCloseable c) {
        if (c == null) return;
        try { c.close(); } catch (Exception ignored) {}
    }
}

