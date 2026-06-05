package org.example.yptapp.model;

public class Task {
    private int id;
    private int subjectId;
    private String subjectName;
    private String title;
    private String description;
    private int maxScore;
    private String type; // homework, test, project, exam
    private String deadline;

    public Task() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}