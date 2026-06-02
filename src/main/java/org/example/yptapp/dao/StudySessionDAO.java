package org.example.yptapp.dao;

import org.example.yptapp.model.StudySession;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudySessionDAO {
    public int startSession(int studentId, int subjectId) throws SQLException {
        String sql = "INSERT INTO study_sessions (student_id, subject_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public void endSession(int sessionId) throws SQLException {
        String sql = "UPDATE study_sessions SET end_time=NOW(), duration_seconds=TIMESTAMPDIFF(SECOND, start_time, NOW()) WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        }
    }

    public List<StudySession> getByStudent(int studentId) throws SQLException {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT ss.*, s.name as subject_name FROM study_sessions ss LEFT JOIN subjects s ON ss.subject_id = s.id WHERE ss.student_id = ? ORDER BY ss.start_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private StudySession map(ResultSet rs) throws SQLException {
        StudySession s = new StudySession();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_time");
        s.setEndTime(end != null ? end.toLocalDateTime() : null);
        s.setDurationSeconds(rs.getInt("duration_seconds"));
        return s;
    }
}