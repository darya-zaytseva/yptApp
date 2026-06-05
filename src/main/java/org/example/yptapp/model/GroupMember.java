package org.example.yptapp.model;

public class GroupMember {
    private int id;
    private int groupId;
    private int studentId;
    private String studentName;
    private String groupName;
    private String role; // organizer, member, observer

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}