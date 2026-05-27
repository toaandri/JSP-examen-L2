package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public final class ProfileDao {
    public static final class Profile {
        private final long userId;
        private final String firstName;
        private final String lastName;
        private final LocalDate birthdate;
        private final String genderIdentity;
        private final String sexualOrientation;
        private final String lookingFor;
        private final String bio;
        private final String city;

        public Profile(long userId, String firstName, String lastName, LocalDate birthdate,
                       String genderIdentity, String sexualOrientation, String lookingFor,
                       String bio, String city) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthdate = birthdate;
            this.genderIdentity = genderIdentity;
            this.sexualOrientation = sexualOrientation;
            this.lookingFor = lookingFor;
            this.bio = bio;
            this.city = city;
        }

        public long getUserId() { return userId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public LocalDate getBirthdate() { return birthdate; }
        public String getGenderIdentity() { return genderIdentity; }
        public String getSexualOrientation() { return sexualOrientation; }
        public String getLookingFor() { return lookingFor; }
        public String getBio() { return bio; }
        public String getCity() { return city; }
    }

    public Optional<Profile> findByUserId(Connection c, long userId) throws SQLException {
        String sql =
                "SELECT user_id, first_name, last_name, birthdate, gender_identity, sexual_orientation, looking_for, bio, city " +
                "FROM profile " +
                "WHERE user_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Profile(
                        rs.getLong("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birthdate").toLocalDate(),
                        rs.getString("gender_identity"),
                        rs.getString("sexual_orientation"),
                        rs.getString("looking_for"),
                        rs.getString("bio"),
                        rs.getString("city")
                ));
            }
        }
    }

    public void upsertIdentity(Connection c, long userId, String firstName, String lastName, LocalDate birthdate) throws SQLException {
        String sql =
                "INSERT INTO profile(user_id, first_name, last_name, birthdate, gender_identity, sexual_orientation, looking_for) " +
                "VALUES (?, ?, ?, ?, 'Non précisé', 'Non précisé', 'Tous') " +
                "ON CONFLICT (user_id) DO UPDATE SET " +
                "  first_name = EXCLUDED.first_name, " +
                "  last_name = EXCLUDED.last_name, " +
                "  birthdate = EXCLUDED.birthdate, " +
                "  updated_at = now()";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setDate(4, Date.valueOf(birthdate));
            ps.executeUpdate();
        }
    }

    public void updateGenderAndOrientation(Connection c, long userId, String genderIdentity, String sexualOrientation, String lookingFor) throws SQLException {
        String sql = "UPDATE profile SET gender_identity=?, sexual_orientation=?, looking_for=?, updated_at=now() WHERE user_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, genderIdentity);
            ps.setString(2, sexualOrientation);
            ps.setString(3, lookingFor);
            ps.setLong(4, userId);
            ps.executeUpdate();
        }
    }

    public void updateBioCity(Connection c, long userId, String bio, String city) throws SQLException {
        String sql = "UPDATE profile SET bio=?, city=?, updated_at=now() WHERE user_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bio);
            ps.setString(2, city);
            ps.setLong(3, userId);
            ps.executeUpdate();
        }
    }

    public boolean isOnboardingComplete(Profile p) {
        if (p == null) return false;
        return notBlank(p.getFirstName()) && notBlank(p.getLastName()) && p.getBirthdate() != null
                && notBlank(p.getGenderIdentity()) && notBlank(p.getSexualOrientation()) && notBlank(p.getLookingFor());
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}

