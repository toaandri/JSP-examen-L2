package mg.univ.tinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OnboardingQuestionDao {
    public static final class Question {
        private final long id;
        private final String key;
        private final String label;
        private final int order;

        public Question(long id, String key, String label, int order) {
            this.id = id;
            this.key = key;
            this.label = label;
            this.order = order;
        }

        public long getId() { return id; }
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public int getOrder() { return order; }
    }

    public static final class Option {
        private final long id;
        private final long questionId;
        private final String label;
        private final String scoreTag;
        private final int order;

        public Option(long id, long questionId, String label, String scoreTag, int order) {
            this.id = id;
            this.questionId = questionId;
            this.label = label;
            this.scoreTag = scoreTag;
            this.order = order;
        }

        public long getId() { return id; }
        public long getQuestionId() { return questionId; }
        public String getLabel() { return label; }
        public String getScoreTag() { return scoreTag; }
        public int getOrder() { return order; }
    }

    public List<Question> listActiveQuestions(Connection c) throws SQLException {
        String sql = "SELECT id, question_key, label, question_order FROM onboarding_question WHERE is_active=true ORDER BY question_order";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Question> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Question(
                        rs.getLong("id"),
                        rs.getString("question_key"),
                        rs.getString("label"),
                        rs.getInt("question_order")
                ));
            }
            return out;
        }
    }

    public List<Option> listOptionsForQuestion(Connection c, long questionId) throws SQLException {
        String sql = "SELECT id, question_id, label, score_tag, option_order FROM onboarding_option WHERE question_id=? ORDER BY option_order";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Option> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Option(
                            rs.getLong("id"),
                            rs.getLong("question_id"),
                            rs.getString("label"),
                            rs.getString("score_tag"),
                            rs.getInt("option_order")
                    ));
                }
                return out;
            }
        }
    }

    public void upsertUserAnswer(Connection c, long userId, long questionId, long optionId) throws SQLException {
        String sql = "INSERT INTO user_onboarding_answer(user_id, question_id, option_id) VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id, question_id) DO UPDATE SET option_id=EXCLUDED.option_id, answered_at=now()";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            ps.setLong(3, optionId);
            ps.executeUpdate();
        }
    }

    public Set<String> setScoreTagsForUser(Connection c, long userId) throws SQLException {
        String sql = "SELECT o.score_tag FROM user_onboarding_answer a JOIN onboarding_option o ON o.id=a.option_id WHERE a.user_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                Set<String> out = new HashSet<>();
                while (rs.next()) out.add(rs.getString(1));
                return out;
            }
        }
    }

    public int countActiveQuestions(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM onboarding_question WHERE is_active=true");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int countAnswersForUser(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM user_onboarding_answer WHERE user_id=?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}

