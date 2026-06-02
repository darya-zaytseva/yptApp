package org.example.yptapp.dao;

import org.example.yptapp.model.Achievement;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

    public List<Achievement> getUserAchievements(int userId) throws SQLException {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT a.*, ua.earned_at FROM achievements a " +
                "JOIN user_achievements ua ON a.id = ua.achievement_id " +
                "WHERE ua.user_id = ? ORDER BY ua.earned_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Achievement a = map(rs);
                a.setEarnedAt(rs.getTimestamp("earned_at").toLocalDateTime());
                list.add(a);
            }
        }
        return list;
    }

    public List<Achievement> getAll() throws SQLException {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT * FROM achievements";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Проверяет условия достижений и выдаёт их автоматически.
     * Вызывается после каждой значимой операции (конец сессии, сдача задания и т.д.)
     */
    public void checkAndAward(int studentId, int userId) throws SQLException {
        // 1. Первые шаги — 1 сессия учёбы
        awardIf(sqlSessionCount(studentId, userId, 1, "sessions", 1));

        // 2. Марафонец — 10 часов учёбы
        awardIf(sqlHoursTotal(studentId, userId, 10, "hours", 10));

        // 3. Отличник — средний балл >= 4.5 (храним как 45 в БД)
        awardIf(sqlAvgScore(studentId, userId, 45, "score", 45));

        // 4. Трудоголик — 5 выполненных заданий
        awardIf(sqlCompletedTasks(studentId, userId, 5, "tasks", 5));
    }

    private void awardIf(String sql) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    // SQL для достижения "сессии" (количество сессий)
    private String sqlSessionCount(int studentId, int userId, int minCount, String conditionType, int conditionValue) {
        return String.format(
                "INSERT INTO user_achievements (user_id, achievement_id) " +
                        "SELECT %d, a.id FROM achievements a " +
                        "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = %d " +
                        "WHERE a.condition_type = '%s' AND a.condition_value = %d " +
                        "AND (SELECT COUNT(*) FROM study_sessions WHERE student_id = %d) >= %d " +
                        "AND ua.id IS NULL",
                userId, userId, conditionType, conditionValue, studentId, minCount
        );
    }

    // SQL для достижения "часы" (суммарное время)
    private String sqlHoursTotal(int studentId, int userId, int minHours, String conditionType, int conditionValue) {
        return String.format(
                "INSERT INTO user_achievements (user_id, achievement_id) " +
                        "SELECT %d, a.id FROM achievements a " +
                        "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = %d " +
                        "WHERE a.condition_type = '%s' AND a.condition_value = %d " +
                        "AND (SELECT COALESCE(SUM(duration_seconds)/3600, 0) FROM study_sessions WHERE student_id = %d) >= %d " +
                        "AND ua.id IS NULL",
                userId, userId, conditionType, conditionValue, studentId, minHours
        );
    }

    // SQL для достижения "score" (средний балл * 10, т.к. в БД храним целые)
    private String sqlAvgScore(int studentId, int userId, int minScoreX10, String conditionType, int conditionValue) {
        return String.format(
                "INSERT INTO user_achievements (user_id, achievement_id) " +
                        "SELECT %d, a.id FROM achievements a " +
                        "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = %d " +
                        "WHERE a.condition_type = '%s' AND a.condition_value = %d " +
                        "AND (SELECT COALESCE(AVG(score)*10, 0) FROM task_submissions WHERE student_id = %d AND score IS NOT NULL) >= %d " +
                        "AND ua.id IS NULL",
                userId, userId, conditionType, conditionValue, studentId, minScoreX10
        );
    }

    // SQL для достижения "tasks" (выполненные задания)
    private String sqlCompletedTasks(int studentId, int userId, int minTasks, String conditionType, int conditionValue) {
        return String.format(
                "INSERT INTO user_achievements (user_id, achievement_id) " +
                        "SELECT %d, a.id FROM achievements a " +
                        "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = %d " +
                        "WHERE a.condition_type = '%s' AND a.condition_value = %d " +
                        "AND (SELECT COUNT(*) FROM task_submissions ts JOIN tasks t ON ts.task_id = t.id WHERE ts.student_id = %d AND ts.status = 'completed') >= %d " +
                        "AND ua.id IS NULL",
                userId, userId, conditionType, conditionValue, studentId, minTasks
        );
    }

    private Achievement map(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setIcon(rs.getString("icon"));
        a.setConditionType(rs.getString("condition_type"));
        a.setConditionValue(rs.getInt("condition_value"));
        return a;
    }
}