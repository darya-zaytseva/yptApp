package org.example.yptapp.dao;

import org.example.yptapp.model.Group;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    public List<Group> getAll() throws SQLException {
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM `groups` ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Group g) throws SQLException {
        String sql = "INSERT INTO `groups` (name, specialty, year, privacy, max_members, creator_id, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getSpecialty());
            ps.setInt(3, g.getYear());
            ps.setString(4, g.getPrivacy());
            ps.setInt(5, g.getMaxMembers());
            ps.setObject(6, g.getCreatorId());
            ps.setString(7, g.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Group g) throws SQLException {
        String sql = "UPDATE `groups` SET name=?, specialty=?, year=?, privacy=?, max_members=?, creator_id=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getSpecialty());
            ps.setInt(3, g.getYear());
            ps.setString(4, g.getPrivacy());
            ps.setInt(5, g.getMaxMembers());
            ps.setObject(6, g.getCreatorId());
            ps.setString(7, g.getDescription());
            ps.setInt(8, g.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `groups` WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Group map(ResultSet rs) throws SQLException {
        Group g = new Group();
        g.setId(rs.getInt("id"));
        g.setName(rs.getString("name"));
        g.setSpecialty(rs.getString("specialty"));
        g.setYear(rs.getInt("year"));
        g.setPrivacy(rs.getString("privacy"));
        g.setMaxMembers(rs.getInt("max_members"));
        g.setCreatorId((Integer) rs.getObject("creator_id"));
        g.setDescription(rs.getString("description"));
        return g;
    }
}