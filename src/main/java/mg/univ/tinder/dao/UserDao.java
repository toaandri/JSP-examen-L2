package mg.univ.tinder.dao;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public final class UserDao {
    public static final class User {
        private final long id;
        private final String email;
        private final String passwordHash;
        private final Instant createdAt;

        public User(long id, String email, String passwordHash, Instant createdAt) {
            this.id = id;
            this.email = email;
            this.passwordHash = passwordHash;
            this.createdAt = createdAt;
        }

        public long getId() { return id; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
        public Instant getCreatedAt() { return createdAt; }
    }

    public Optional<User> findByEmail(Connection c, String email) throws SQLException {
        String sql = "SELECT id, email, password_hash, created_at FROM app_user WHERE email = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        toInstant(rs.getTimestamp("created_at"))
                ));
            }
        }
    }

    public long create(Connection c, String email, String rawPassword) throws SQLException {
        String sql = "INSERT INTO app_user(email, password_hash) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public boolean verifyPasswordAndUpgradeSeedIfNeeded(Connection c, long userId, String storedHash, String rawPassword) throws SQLException {
        if (storedHash != null && storedHash.startsWith("{SEED}")) {
            String seedRaw = storedHash.substring("{SEED}".length());
            if (!seedRaw.equals(rawPassword)) return false;
            String newHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
            updatePasswordHash(c, userId, newHash);
            return true;
        }
        return storedHash != null && BCrypt.checkpw(rawPassword, storedHash);
    }

    public void updatePasswordHash(Connection c, long userId, String newHash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE app_user SET password_hash=? WHERE id=?")) {
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void touchLastLogin(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE app_user SET last_login_at=now() WHERE id=?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    private static Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
}

