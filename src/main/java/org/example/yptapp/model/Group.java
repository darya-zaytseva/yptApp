package org.example.yptapp.model;

public class Group {
    private int id;
    private String name;
    private String specialty;
    private int year;
    private String privacy = "open";
    private int maxMembers = 50;
    private Integer creatorId;
    private String description;
    public Group() {}
    public Group(int id, String name, String specialty, int year) {
        this.id = id; this.name = name; this.specialty = specialty; this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }
    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public Integer getCreatorId() { return creatorId; }
    public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @Override
    public String toString() { return name; }
}