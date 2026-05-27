package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class BlockDao {
    public void block(Connection c, long blockerUserId, long blockedUserId) throws SQLException {
        String sql =
                "INSERT INTO block(blocker_user_id, blocked_user_id) VALUES (?, ?) " +
                "ON CONFLICT (blocker_user_id, blocked_user_id) DO NOTHING";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, blockerUserId);
            ps.setLong(2, blockedUserId);
            ps.executeUpdate();
        }
    }
}

