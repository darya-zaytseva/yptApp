package org.example.yptapp.model;

public class Schedule {
    private int id;
    private int groupId;
    private String groupName;
    private int subjectId;
    private String subjectName;
    private int dayOfWeek;
    private int pairNumber;
    private String room;

    public Schedule() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public int getPairNumber() { return pairNumber; }
    public void setPairNumber(int pairNumber) { this.pairNumber = pairNumber; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}