package org.example.yptapp.model;

public class Subject {
    private int id;
    private String name;
    private String code;
    private int hours;

    public Subject() {}
    public Subject(int id, String name, String code, int hours) {
        this.id = id; this.name = name; this.code = code; this.hours = hours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    @Override
    public String toString() { return name; }
}