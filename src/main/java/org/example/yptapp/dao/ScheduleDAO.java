package org.example.yptapp.dao;

import org.example.yptapp.model.Schedule;
import org.example.yptapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    public List<Schedule> getAll() throws SQLException {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT sch.*, g.name as group_name, sub.name as subject_name FROM `schedule` sch " +
                "LEFT JOIN `groups` g ON sch.group_id = g.id " +
                "LEFT JOIN subjects sub ON sch.subject_id = sub.id ORDER BY sch.day_of_week, sch.pair_number";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Schedule s) throws SQLException {
        String sql = "INSERT INTO `schedule` (group_id, subject_id, day_of_week, pair_number, room) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getGroupId());
            ps.setInt(2, s.getSubjectId());
            ps.setInt(3, s.getDayOfWeek());
            ps.setInt(4, s.getPairNumber());
            ps.setString(5, s.getRoom());
            ps.executeUpdate();
        }
    }

    public void update(Schedule s) throws SQLException {
        String sql = "UPDATE `schedule` SET group_id=?, subject_id=?, day_of_week=?, pair_number=?, room=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getGroupId());
            ps.setInt(2, s.getSubjectId());
            ps.setInt(3, s.getDayOfWeek());
            ps.setInt(4, s.getPairNumber());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `schedule` WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Schedule map(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getInt("id"));
        s.setGroupId(rs.getInt("group_id"));
        s.setGroupName(rs.getString("group_name"));
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setDayOfWeek(rs.getInt("day_of_week"));
        s.setPairNumber(rs.getInt("pair_number"));
        s.setRoom(rs.getString("room"));
        return s;
    }
}