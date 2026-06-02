package org.example.yptapp.model;

import java.time.LocalDateTime;

public class Friendship {
    private int id;
    private int userId1;
    private int userId2;
    private String status; // request, friends, blocked
    private LocalDateTime createdAt;
    private String userName1;
    private String userName2;

    public Friendship() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId1() { return userId1; }
    public void setUserId1(int userId1) { this.userId1 = userId1; }
    public int getUserId2() { return userId2; }
    public void setUserId2(int userId2) { this.userId2 = userId2; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUserName1() { return userName1; }
    public void setUserName1(String userName1) { this.userName1 = userName1; }
    public String getUserName2() { return userName2; }
    public void setUserName2(String userName2) { this.userName2 = userName2; }

    public String getOtherUserName(int currentUserId) {
        return currentUserId == userId1 ? userName2 : userName1;
    }
    public int getOtherUserId(int currentUserId) {
        return currentUserId == userId1 ? userId2 : userId1;
    }
}