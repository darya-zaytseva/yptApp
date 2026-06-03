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

    /**
     * Проверяет условия достижений и выдаёт их автоматически.
     * Вызывается после каждой значимой операции (конец сессии, сдача задания и т.д.)
     */
    public void checkAndAward(int studentId, int userId) throws SQLException {
        // 1. Первые шаги — 1 сессия учёбы
        awardIf(userId, "sessions", 1,
                "SELECT COUNT(*) FROM study_sessions WHERE student_id = ?", studentId);

        // 2. Марафонец — 10 часов учёбы
        awardIf(userId, "hours", 10,
                "SELECT COALESCE(SUM(duration_seconds)/3600, 0) FROM study_sessions WHERE student_id = ?", studentId);

        // 3. Отличник — средний балл >= 4.5 (храним как 45 в БД)
        awardIf(userId, "score", 45,
                "SELECT COALESCE(AVG(score)*10, 0) FROM task_submissions WHERE student_id = ? AND score IS NOT NULL", studentId);

        // 4. Трудоголик — 5 выполненных заданий
        awardIf(userId, "tasks", 5,
                "SELECT COUNT(*) FROM task_submissions ts JOIN tasks t ON ts.task_id = t.id WHERE ts.student_id = ? AND ts.status = 'completed'", studentId);
    }

    private void awardIf(int userId, String conditionType, int conditionValue, String countSql, int studentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement countPs = conn.prepareStatement(countSql)) {
            countPs.setInt(1, studentId);
            ResultSet countRs = countPs.executeQuery();
            if (!countRs.next() || countRs.getInt(1) < conditionValue) {
                return; // Условие не выполнено
            }
        }

        // Условие выполнено — выдаём достижение (если ещё не выдано)
        String insertSql = "INSERT INTO user_achievements (user_id, achievement_id) " +
                "SELECT ?, a.id FROM achievements a " +
                "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = ? " +
                "WHERE a.condition_type = ? AND a.condition_value = ? AND ua.id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, conditionType);
            ps.setInt(4, conditionValue);
            ps.executeUpdate();
        }
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