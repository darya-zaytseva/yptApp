package org.example.yptapp.dao;

import org.example.yptapp.model.Material;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {
    public List<Material> getAll() throws SQLException {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name as subject_name FROM materials m LEFT JOIN subjects s ON m.subject_id = s.id ORDER BY m.uploaded_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Material> getBySubject(int subjectId) throws SQLException {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name as subject_name FROM materials m LEFT JOIN subjects s ON m.subject_id = s.id WHERE m.subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Material m) throws SQLException {
        String sql = "INSERT INTO materials (subject_id, title, file_path, file_name, file_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getSubjectId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getFilePath());
            ps.setString(4, m.getFileName());
            ps.setString(5, m.getFileType());
            ps.executeUpdate();
        }
    }

    /** Перегруженный метод для использования в транзакции */
    public void add(Connection conn, Material m) throws SQLException {
        String sql = "INSERT INTO materials (subject_id, title, file_path, file_name, file_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getSubjectId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getFilePath());
            ps.setString(4, m.getFileName());
            ps.setString(5, m.getFileType());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM materials WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Material map(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setId(rs.getInt("id"));
        m.setSubjectId(rs.getInt("subject_id"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setTitle(rs.getString("title"));
        m.setFilePath(rs.getString("file_path"));
        m.setFileName(rs.getString("file_name"));
        m.setFileType(rs.getString("file_type"));
        m.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return m;
    }
}