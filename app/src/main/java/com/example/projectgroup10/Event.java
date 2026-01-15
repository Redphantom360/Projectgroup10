package com.example.projectgroup10;

public class Event {
    private String title;
    private String location;
    private int imageResource;

    public Event(String title, String location, int imageResource) {
        this.title = title;
        this.location = location;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public int getImageResource() {
        return imageResource;
    }
}
