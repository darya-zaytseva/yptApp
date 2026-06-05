package org.example.yptapp.dao;

import org.example.yptapp.model.TaskSubmission;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskSubmissionDAO {
    public List<TaskSubmission> getByStudent(int studentId) throws SQLException {
        List<TaskSubmission> list = new ArrayList<>();
        String sql = "SELECT ts.*, t.title as task_title, s.first_name, s.last_name " +
                "FROM task_submissions ts " +
                "JOIN tasks t ON ts.task_id = t.id " +
                "JOIN students s ON ts.student_id = s.id " +
                "WHERE ts.student_id = ? ORDER BY ts.submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void addOrUpdate(TaskSubmission ts) throws SQLException {
        String sql = "INSERT INTO task_submissions (task_id, student_id, status, score, comment) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status=VALUES(status), score=VALUES(score), comment=VALUES(comment), submitted_at=NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ts.getTaskId());
            ps.setInt(2, ts.getStudentId());
            ps.setString(3, ts.getStatus());
            ps.setObject(4, ts.getScore());
            ps.setString(5, ts.getComment());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM task_submissions WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private TaskSubmission map(ResultSet rs) throws SQLException {
        TaskSubmission ts = new TaskSubmission();
        ts.setId(rs.getInt("id"));
        ts.setTaskId(rs.getInt("task_id"));
        ts.setStudentId(rs.getInt("student_id"));
        ts.setTaskTitle(rs.getString("task_title"));
        ts.setStudentName(rs.getString("last_name") + " " + rs.getString("first_name"));
        ts.setStatus(rs.getString("status"));
        ts.setScore((Integer) rs.getObject("score"));
        ts.setComment(rs.getString("comment"));
        Timestamp sa = rs.getTimestamp("submitted_at");
        ts.setSubmittedAt(sa != null ? sa.toLocalDateTime() : null);
        return ts;
    }
}