package com.example.projectgroup10;

public class StepRecord {

    public String id;       // Unique key for Firebase
    public String date;
    public int steps;
    public float distance;
    public float co2;

    // Required empty constructor for Firebase
    public StepRecord() {
    }

    // Full constructor
    public StepRecord(String id, String date, int steps, float distance, float co2) {
        this.id = id;
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.co2 = co2;
    }

    // Convenience constructor: use date as id automatically
    public StepRecord(String date, int steps, float distance, float co2) {
        this.id = date;  // use date as unique id
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.co2 = co2;
    }
}
