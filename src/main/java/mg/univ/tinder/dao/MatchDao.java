package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MatchDao {
    public static final class MatchRow {
        private final long matchId;
        private final long otherUserId;
        private final String otherName;
        private final String otherPhotoUrl;

        public MatchRow(long matchId, long otherUserId, String otherName, String otherPhotoUrl) {
            this.matchId = matchId;
            this.otherUserId = otherUserId;
            this.otherName = otherName;
            this.otherPhotoUrl = otherPhotoUrl;
        }

        public long getMatchId() { return matchId; }
        public long getOtherUserId() { return otherUserId; }
        public String getOtherName() { return otherName; }
        public String getOtherPhotoUrl() { return otherPhotoUrl; }
    }

    public List<MatchRow> listForUser(Connection c, long userId) throws SQLException {
        String sql =
                "SELECT m.id AS match_id, " +
                "       CASE WHEN m.user_a_id=? THEN m.user_b_id ELSE m.user_a_id END AS other_user_id, " +
                "       p.first_name AS other_first_name, p.photo_url AS other_photo_url " +
                "FROM app_match m " +
                "JOIN profile p ON p.user_id = (CASE WHEN m.user_a_id=? THEN m.user_b_id ELSE m.user_a_id END) " +
                "WHERE m.user_a_id=? OR m.user_b_id=? " +
                "ORDER BY m.created_at DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<MatchRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new MatchRow(
                            rs.getLong("match_id"),
                            rs.getLong("other_user_id"),
                            rs.getString("other_first_name"),
                            rs.getString("other_photo_url")
                    ));
                }
                return out;
            }
        }
    }
}

