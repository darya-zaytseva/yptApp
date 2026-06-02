package org.example.yptapp.dao;

import org.example.yptapp.model.Subject;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public List<Subject> getAll() throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Subject s) throws SQLException {
        String sql = "INSERT INTO subjects (name, code, hours) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCode());
            ps.setInt(3, s.getHours());
            ps.executeUpdate();
        }
    }

    public void update(Subject s) throws SQLException {
        String sql = "UPDATE subjects SET name=?, code=?, hours=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCode());
            ps.setInt(3, s.getHours());
            ps.setInt(4, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Subject map(ResultSet rs) throws SQLException {
        return new Subject(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getInt("hours")
        );
    }
}