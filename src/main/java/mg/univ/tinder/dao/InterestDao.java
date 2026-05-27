package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class InterestDao {
    public static final class Interest {
        private final long id;
        private final String label;

        public Interest(long id, String label) {
            this.id = id;
            this.label = label;
        }

        public long getId() { return id; }
        public String getLabel() { return label; }
    }

    public List<Interest> listAll(Connection c) throws SQLException {
        String sql = "SELECT id, label FROM interest ORDER BY label";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Interest> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Interest(rs.getLong("id"), rs.getString("label")));
            }
            return out;
        }
    }

    public void replaceUserInterests(Connection c, long userId, List<Long> interestIds) throws SQLException {
        try (PreparedStatement del = c.prepareStatement("DELETE FROM user_interest WHERE user_id=?")) {
            del.setLong(1, userId);
            del.executeUpdate();
        }

        if (interestIds == null || interestIds.isEmpty()) return;

        try (PreparedStatement ins = c.prepareStatement("INSERT INTO user_interest(user_id, interest_id) VALUES (?, ?)")) {
            for (Long id : interestIds) {
                if (id == null) continue;
                ins.setLong(1, userId);
                ins.setLong(2, id);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }
}

