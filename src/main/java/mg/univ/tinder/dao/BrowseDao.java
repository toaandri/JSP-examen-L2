package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public final class BrowseDao {
    public static final class Candidate {
        private final long userId;
        private final String firstName;
        private final int age;
        private final String city;
        private final String bio;
        private final String photoUrl;

        public Candidate(long userId, String firstName, int age, String city, String bio, String photoUrl) {
            this.userId = userId;
            this.firstName = firstName;
            this.age = age;
            this.city = city;
            this.bio = bio;
            this.photoUrl = photoUrl;
        }

        public long getUserId() { return userId; }
        public String getFirstName() { return firstName; }
        public int getAge() { return age; }
        public String getCity() { return city; }
        public String getBio() { return bio; }
        public String getPhotoUrl() { return photoUrl; }
    }

    public Optional<Candidate> pickNextCandidate(Connection c, long viewerUserId) throws SQLException {
        String sql =
                "SELECT p.user_id, p.first_name, p.birthdate, p.city, p.bio, p.photo_url " +
                "FROM profile p " +
                "WHERE p.user_id <> ? " +
                "  AND NOT EXISTS (SELECT 1 FROM swipe s WHERE s.from_user_id = ? AND s.to_user_id = p.user_id) " +
                "  AND NOT EXISTS (SELECT 1 FROM block b WHERE b.blocker_user_id = ? AND b.blocked_user_id = p.user_id) " +
                "ORDER BY random() " +
                "LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, viewerUserId);
            ps.setLong(2, viewerUserId);
            ps.setLong(3, viewerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                long uid = rs.getLong("user_id");
                LocalDate birth = rs.getDate("birthdate").toLocalDate();
                int age = ageFromBirthdate(birth);
                return Optional.of(new Candidate(
                        uid,
                        rs.getString("first_name"),
                        age,
                        rs.getString("city"),
                        rs.getString("bio"),
                        rs.getString("photo_url")
                ));
            }
        }
    }

    private static int ageFromBirthdate(LocalDate birth) {
        if (birth == null) return 0;
        LocalDate now = LocalDate.now();
        int years = now.getYear() - birth.getYear();
        if (birth.plusYears(years).isAfter(now)) years--;
        return Math.max(0, years);
    }
}

