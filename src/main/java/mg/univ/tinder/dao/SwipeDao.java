package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class SwipeDao {
    public enum Decision { LIKE, NOPE }

    public void upsertSwipe(Connection c, long fromUserId, long toUserId, Decision decision) throws SQLException {
        String sql =
                "INSERT INTO swipe(from_user_id, to_user_id, decision) VALUES (?, ?, ?) " +
                "ON CONFLICT (from_user_id, to_user_id) DO UPDATE SET decision=EXCLUDED.decision, created_at=now()";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, fromUserId);
            ps.setLong(2, toUserId);
            ps.setString(3, decision.name());
            ps.executeUpdate();
        }
    }

    public boolean hasLiked(Connection c, long fromUserId, long toUserId) throws SQLException {
        String sql = "SELECT 1 FROM swipe WHERE from_user_id=? AND to_user_id=? AND decision='LIKE'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, fromUserId);
            ps.setLong(2, toUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<Long> ensureMatchIfMutualLike(Connection c, long user1, long user2) throws SQLException {
        long a = Math.min(user1, user2);
        long b = Math.max(user1, user2);
        String ins =
                "INSERT INTO app_match(user_a_id, user_b_id) VALUES (?, ?) " +
                "ON CONFLICT (user_a_id, user_b_id) DO NOTHING " +
                "RETURNING id";
        try (PreparedStatement ps = c.prepareStatement(ins)) {
            ps.setLong(1, a);
            ps.setLong(2, b);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getLong(1));
            }
        }
        // If it already existed, fetch id
        String sel = "SELECT id FROM app_match WHERE user_a_id=? AND user_b_id=?";
        try (PreparedStatement ps = c.prepareStatement(sel)) {
            ps.setLong(1, a);
            ps.setLong(2, b);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getLong(1));
                return Optional.empty();
            }
        }
    }
}

