package com.example.projectgroup10;

public class StepRecord {
    private String date;
    private int steps;
    private float distance;
    private float co2Saved;

    // Required for Firebase
    public StepRecord() {}

    public StepRecord(String date, int steps, float distance, float co2Saved) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.co2Saved = co2Saved;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public float getDistance() {
        return distance;
    }

    public float getCo2Saved() {
        return co2Saved;
    }
}
