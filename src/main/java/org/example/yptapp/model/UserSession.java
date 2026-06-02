package org.example.yptapp.model;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String username;
    private String role;
    private String fullName;
    private Integer studentId;
    private String avatarUrl;
    private String interests;
    private String goals;
    private String level;
    private String location;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public boolean isAdmin() { return "admin".equals(role); }
    public boolean isTeacher() { return "teacher".equals(role); }
    public boolean isStudent() { return "student".equals(role); }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}