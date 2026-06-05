package org.example.yptapp.dao;

import org.example.yptapp.model.Task;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public List<Task> getAll() throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subject_name FROM tasks t LEFT JOIN subjects s ON t.subject_id = s.id ORDER BY t.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Task> getBySubject(int subjectId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subject_name FROM tasks t LEFT JOIN subjects s ON t.subject_id = s.id WHERE t.subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Task t) throws SQLException {
        String sql = "INSERT INTO tasks (subject_id, title, description, max_score, type, deadline) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getSubjectId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getDescription());
            ps.setInt(4, t.getMaxScore());
            ps.setString(5, t.getType());
            ps.setString(6, t.getDeadline());
            ps.executeUpdate();
        }
    }

    public void update(Task t) throws SQLException {
        String sql = "UPDATE tasks SET subject_id=?, title=?, description=?, max_score=?, type=?, deadline=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getSubjectId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getDescription());
            ps.setInt(4, t.getMaxScore());
            ps.setString(5, t.getType());
            ps.setString(6, t.getDeadline());
            ps.setInt(7, t.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Task map(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setSubjectId(rs.getInt("subject_id"));
        t.setSubjectName(rs.getString("subject_name"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setMaxScore(rs.getInt("max_score"));
        t.setType(rs.getString("type"));
        t.setDeadline(rs.getString("deadline"));
        return t;
    }
}