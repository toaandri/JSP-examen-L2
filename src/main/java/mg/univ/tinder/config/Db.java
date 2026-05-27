package mg.univ.tinder.config;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public final class Db {
    private static volatile DataSource dataSource;

    private Db() {}

    public static DataSource getDataSource() {
        DataSource local = dataSource;
        if (local != null) return local;
        synchronized (Db.class) {
            if (dataSource != null) return dataSource;

            String url = env("APP_DB_URL", "jdbc:postgresql://localhost:5432/tinder_jsp");
            String user = env("APP_DB_USER", "postgres");
            String pass = env("APP_DB_PASSWORD", "postgres");

            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(pass);

            dataSource = ds;
            return ds;
        }
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? fallback : v;
    }
}

