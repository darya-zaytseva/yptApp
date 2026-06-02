package org.example.yptapp.dao;

import org.example.yptapp.model.Student;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, g.name as group_name FROM students s LEFT JOIN `groups` g ON s.group_id = g.id ORDER BY s.last_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Student> getByGroup(int groupId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, g.name as group_name FROM students s LEFT JOIN `groups` g ON s.group_id = g.id WHERE s.group_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Student getByUserId(int userId) throws SQLException {
        String sql = "SELECT s.*, g.name as group_name FROM students s LEFT JOIN `groups` g ON s.group_id = g.id WHERE s.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO students (user_id, group_id, first_name, last_name, middle_name, birth_date, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, s.getUserId());
            ps.setInt(2, s.getGroupId());
            ps.setString(3, s.getFirstName());
            ps.setString(4, s.getLastName());
            ps.setString(5, s.getMiddleName());
            ps.setDate(6, s.getBirthDate() != null ? Date.valueOf(s.getBirthDate()) : null);
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE students SET user_id=?, group_id=?, first_name=?, last_name=?, middle_name=?, birth_date=?, phone=?, email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, s.getUserId());
            ps.setInt(2, s.getGroupId());
            ps.setString(3, s.getFirstName());
            ps.setString(4, s.getLastName());
            ps.setString(5, s.getMiddleName());
            ps.setDate(6, s.getBirthDate() != null ? Date.valueOf(s.getBirthDate()) : null);
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getEmail());
            ps.setInt(9, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId((Integer) rs.getObject("user_id"));
        s.setGroupId(rs.getInt("group_id"));
        s.setGroupName(rs.getString("group_name"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setMiddleName(rs.getString("middle_name"));
        Date bd = rs.getDate("birth_date");
        s.setBirthDate(bd != null ? bd.toLocalDate() : null);
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        return s;
    }
}