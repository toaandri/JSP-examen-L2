package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UserInterestDao {
    public List<String> listLabelsForUser(Connection c, long userId) throws SQLException {
        String sql = "SELECT i.label " +
                "FROM user_interest ui " +
                "JOIN interest i ON i.id = ui.interest_id " +
                "WHERE ui.user_id = ? " +
                "ORDER BY i.label";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> out = new ArrayList<>();
                while (rs.next()) out.add(rs.getString(1));
                return out;
            }
        }
    }

    public Set<String> setLabelsForUser(Connection c, long userId) throws SQLException {
        return new HashSet<>(listLabelsForUser(c, userId));
    }
}

