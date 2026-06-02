package org.example.yptapp.dao;

import org.example.yptapp.model.Grade;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    public List<Grade> getAll() throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, s.first_name, s.last_name, sub.name as subject_name FROM grades g " +
                "LEFT JOIN students s ON g.student_id = s.id " +
                "LEFT JOIN subjects sub ON g.subject_id = sub.id ORDER BY g.date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Grade g) throws SQLException {
        String sql = "INSERT INTO grades (student_id, subject_id, grade, type, date, comment) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getSubjectId());
            ps.setDouble(3, g.getGrade());
            ps.setString(4, g.getType());
            ps.setDate(5, Date.valueOf(g.getDate()));
            ps.setString(6, g.getComment());
            ps.executeUpdate();
        }
    }

    public void update(Grade g) throws SQLException {
        String sql = "UPDATE grades SET student_id=?, subject_id=?, grade=?, type=?, date=?, comment=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getSubjectId());
            ps.setDouble(3, g.getGrade());
            ps.setString(4, g.getType());
            ps.setDate(5, Date.valueOf(g.getDate()));
            ps.setString(6, g.getComment());
            ps.setInt(7, g.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Grade map(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setStudentName(rs.getString("last_name") + " " + rs.getString("first_name"));
        g.setSubjectId(rs.getInt("subject_id"));
        g.setSubjectName(rs.getString("subject_name"));
        g.setGrade(rs.getDouble("grade"));
        g.setType(rs.getString("type"));
        Date d = rs.getDate("date");
        g.setDate(d != null ? d.toLocalDate() : null);
        g.setComment(rs.getString("comment"));
        return g;
    }
}