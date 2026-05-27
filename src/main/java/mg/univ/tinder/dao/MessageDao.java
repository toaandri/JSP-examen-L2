package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class MessageDao {
    public static final class MessageRow {
        private final long id;
        private final long matchId;
        private final long fromUserId;
        private final String body;
        private final Instant createdAt;

        public MessageRow(long id, long matchId, long fromUserId, String body, Instant createdAt) {
            this.id = id;
            this.matchId = matchId;
            this.fromUserId = fromUserId;
            this.body = body;
            this.createdAt = createdAt;
        }

        public long getId() { return id; }
        public long getMatchId() { return matchId; }
        public long getFromUserId() { return fromUserId; }
        public String getBody() { return body; }
        public Instant getCreatedAt() { return createdAt; }
    }

    public List<MessageRow> listMessages(Connection c, long matchId, long afterId, int limit) throws SQLException {
        if (limit <= 0) limit = 50;
        String sql = "SELECT id, match_id, from_user_id, body, created_at FROM message WHERE match_id=? AND id>? ORDER BY id ASC LIMIT ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, matchId);
            ps.setLong(2, afterId);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<MessageRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new MessageRow(
                            rs.getLong("id"),
                            rs.getLong("match_id"),
                            rs.getLong("from_user_id"),
                            rs.getString("body"),
                            toInstant(rs.getTimestamp("created_at"))
                    ));
                }
                return out;
            }
        }
    }

    public void sendMessage(Connection c, long matchId, long fromUserId, String body) throws SQLException {
        String sql = "INSERT INTO message(match_id, from_user_id, body) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, matchId);
            ps.setLong(2, fromUserId);
            ps.setString(3, body);
            ps.executeUpdate();
        }
    }

    private static Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
}

