package org.example.yptapp.dao;

import org.example.yptapp.model.GroupMember;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {
    public List<GroupMember> getByGroup(int groupId) throws SQLException {
        List<GroupMember> list = new ArrayList<>();
        String sql = "SELECT gm.*, s.first_name, s.last_name, g.name as group_name " +
                "FROM group_members gm " +
                "JOIN students s ON gm.student_id = s.id " +
                "JOIN `groups` g ON gm.group_id = g.id " +
                "WHERE gm.group_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(GroupMember gm) throws SQLException {
        String sql = "INSERT INTO group_members (group_id, student_id, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gm.getGroupId());
            ps.setInt(2, gm.getStudentId());
            ps.setString(3, gm.getRole());
            ps.executeUpdate();
        }
    }

    public void updateRole(int id, String role) throws SQLException {
        String sql = "UPDATE group_members SET role=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM group_members WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private GroupMember map(ResultSet rs) throws SQLException {
        GroupMember gm = new GroupMember();
        gm.setId(rs.getInt("id"));
        gm.setGroupId(rs.getInt("group_id"));
        gm.setStudentId(rs.getInt("student_id"));
        gm.setGroupName(rs.getString("group_name"));
        gm.setStudentName(rs.getString("last_name") + " " + rs.getString("first_name"));
        gm.setRole(rs.getString("role"));
        return gm;
    }
}