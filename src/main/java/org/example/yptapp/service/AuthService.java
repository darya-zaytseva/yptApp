package org.example.yptapp.service;

import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.DBConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AuthService {
    private static final String FIXED_SALT = "ypt_salt_2026_v2";

    public static boolean login(String username, String password) {
        String sql = "SELECT id, username, role, full_name, avatar_url, interests, goals, level, location, password_hash " +
                "FROM users WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);

                if (!storedHash.equalsIgnoreCase(inputHash)) {
                    return false;
                }

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

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(FIXED_SALT.getBytes());
            byte[] hash = md.digest(password.getBytes());
            // Возвращаем hex-строку (как MySQL SHA2), не Base64!
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}