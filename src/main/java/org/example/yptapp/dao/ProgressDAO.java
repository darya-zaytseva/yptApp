package org.example.yptapp.dao;

import org.example.yptapp.model.Progress;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressDAO {
    public List<Progress> getByStudent(int studentId) throws SQLException {
        List<Progress> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name as subject_name FROM progress p LEFT JOIN subjects s ON p.subject_id = s.id WHERE p.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void updateProgressForAllSubjects(int studentId) throws SQLException {
        // Получаем все предметы и обновляем прогресс для каждого
        String subjectsSql = "SELECT id FROM subjects";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(subjectsSql)) {
            while (rs.next()) {
                int subjectId = rs.getInt("id");
                updateProgress(studentId, subjectId);
            }
        }
    }

    public void updateProgress(int studentId, int subjectId) throws SQLException {
        String sql = "INSERT INTO progress (student_id, subject_id, total_hours, completed_tasks, total_tasks, average_score) " +
                "SELECT ?, ?, " +
                "COALESCE((SELECT SUM(duration_seconds)/3600 FROM study_sessions WHERE student_id=? AND subject_id=?), 0), " +
                "COALESCE((SELECT COUNT(*) FROM task_submissions ts JOIN tasks t ON ts.task_id = t.id WHERE ts.student_id=? AND t.subject_id=? AND ts.status='completed'), 0), " +
                "COALESCE((SELECT COUNT(*) FROM tasks WHERE subject_id=?), 0), " +
                "COALESCE((SELECT AVG(score) FROM task_submissions WHERE student_id=? AND score IS NOT NULL), 0) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_hours=VALUES(total_hours), completed_tasks=VALUES(completed_tasks), " +
                "total_tasks=VALUES(total_tasks), average_score=VALUES(average_score)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, studentId);
            ps.setInt(4, subjectId);
            ps.setInt(5, studentId);
            ps.setInt(6, subjectId);
            ps.setInt(7, subjectId);
            ps.setInt(8, studentId);
            ps.executeUpdate();
        }
    }

    private Progress map(ResultSet rs) throws SQLException {
        Progress p = new Progress();
        p.setId(rs.getInt("id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setSubjectId(rs.getInt("subject_id"));
        p.setSubjectName(rs.getString("subject_name"));
        p.setTotalHours(rs.getInt("total_hours"));
        p.setCompletedTasks(rs.getInt("completed_tasks"));
        p.setTotalTasks(rs.getInt("total_tasks"));
        p.setAverageScore(rs.getDouble("average_score"));
        return p;
    }
}