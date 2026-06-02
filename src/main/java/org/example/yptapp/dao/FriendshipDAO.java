package org.example.yptapp.dao;

import org.example.yptapp.model.Friendship;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {

    public List<Friendship> getByUser(int userId) throws SQLException {
        List<Friendship> list = new ArrayList<>();
        String sql = "SELECT f.*, u1.full_name as name1, u2.full_name as name2 " +
                "FROM friendships f " +
                "JOIN users u1 ON f.user_id_1 = u1.id " +
                "JOIN users u2 ON f.user_id_2 = u2.id " +
                "WHERE f.user_id_1 = ? OR f.user_id_2 = ? " +
                "ORDER BY f.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Friendship> getPendingRequests(int userId) throws SQLException {
        List<Friendship> list = new ArrayList<>();
        String sql = "SELECT f.*, u1.full_name as name1, u2.full_name as name2 " +
                "FROM friendships f " +
                "JOIN users u1 ON f.user_id_1 = u1.id " +
                "JOIN users u2 ON f.user_id_2 = u2.id " +
                "WHERE f.user_id_2 = ? AND f.status = 'request'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void sendRequest(int fromUserId, int toUserId) throws SQLException {
        String sql = "INSERT INTO friendships (user_id_1, user_id_2, status) VALUES (?, ?, 'request') " +
                "ON DUPLICATE KEY UPDATE status='request'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.min(fromUserId, toUserId));
            ps.setInt(2, Math.max(fromUserId, toUserId));
            ps.executeUpdate();
        }
    }

    public void accept(int id) throws SQLException {
        String sql = "UPDATE friendships SET status='friends' WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM friendships WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Friendship map(ResultSet rs) throws SQLException {
        Friendship f = new Friendship();
        f.setId(rs.getInt("id"));
        f.setUserId1(rs.getInt("user_id_1"));
        f.setUserId2(rs.getInt("user_id_2"));
        f.setStatus(rs.getString("status"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        f.setUserName1(rs.getString("name1"));
        f.setUserName2(rs.getString("name2"));
        return f;
    }
}