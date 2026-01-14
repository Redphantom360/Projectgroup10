package com.example.project;

public class Announcement {
    public String id;
    public String title;
    public String category;
    public String content;
    public long timestamp;

    public Announcement() {}

    public Announcement(String id, String title, String category, String content, long timestamp) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.timestamp = timestamp;
    }
}
