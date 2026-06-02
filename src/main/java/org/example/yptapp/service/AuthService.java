package org.example.yptapp.service;

import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.DBConnection;
import java.sql.*;

public class AuthService {
    public static boolean login(String username, String password) {
        String sql = "SELECT id, username, role, full_name, avatar_url, interests, goals, level, location " +
                "FROM users WHERE username=? AND password_hash=SHA2(?, 256)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserSession s = UserSession.getInstance();
                s.setUserId(rs.getInt("id"));
                s.setUsername(rs.getString("username"));
                s.setRole(rs.getString("role"));
                s.setFullName(rs.getString("full_name"));
                s.setAvatarUrl(rs.getString("avatar_url"));
                s.setInterests(rs.getString("interests"));
                s.setGoals(rs.getString("goals"));
                s.setLevel(rs.getString("level"));
                s.setLocation(rs.getString("location"));

                if ("student".equals(s.getRole())) {
                    findStudentId(s);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void findStudentId(UserSession session) {
        // Сначала ищем по user_id (надёжно)
        String sql = "SELECT id FROM students WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, session.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                session.setStudentId(rs.getInt("id"));
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fallback по ФИО (для обратной совместимости со старыми данными)
        String sqlFallback = "SELECT id FROM students WHERE first_name=? AND last_name=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFallback)) {
            String[] parts = session.getFullName().split(" ");
            ps.setString(1, parts.length > 1 ? parts[1] : parts[0]);
            ps.setString(2, parts[0]);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                session.setStudentId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}