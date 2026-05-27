package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class NotificationDao {
    public static final class NotificationRow {
        private final long id;
        private final String type;
        private final String payloadJson;
        private final Instant createdAt;
        private final Instant readAt;

        public NotificationRow(long id, String type, String payloadJson, Instant createdAt, Instant readAt) {
            this.id = id;
            this.type = type;
            this.payloadJson = payloadJson;
            this.createdAt = createdAt;
            this.readAt = readAt;
        }

        public long getId() { return id; }
        public String getType() { return type; }
        public String getPayloadJson() { return payloadJson; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getReadAt() { return readAt; }
    }

    public void createNewMatch(Connection c, long userId, long matchId, long withUserId) throws SQLException {
        String payload = "{\"matchId\":" + matchId + ",\"withUserId\":" + withUserId + "}";
        String sql = "INSERT INTO notification(user_id, type, payload_json) VALUES (?, 'NEW_MATCH', ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, payload);
            ps.executeUpdate();
        }
    }

    public List<NotificationRow> listForUser(Connection c, long userId) throws SQLException {
        String sql = "SELECT id, type, payload_json, created_at, read_at FROM notification WHERE user_id=? ORDER BY created_at DESC LIMIT 50";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<NotificationRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new NotificationRow(
                            rs.getLong("id"),
                            rs.getString("type"),
                            rs.getString("payload_json"),
                            toInstant(rs.getTimestamp("created_at")),
                            toInstant(rs.getTimestamp("read_at"))
                    ));
                }
                return out;
            }
        }
    }

    public void markRead(Connection c, long userId, long notificationId) throws SQLException {
        String sql = "UPDATE notification SET read_at=now() WHERE id=? AND user_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private static Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
}

