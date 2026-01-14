package com.example.projectgroup10;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepTrackerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView stepText, distanceText, ecoText, badgeText;
    private ProgressBar stepProgress;
    private Button startBtn, resetBtn, shareBtn, historyBtn, saveBtn;

    private int stepCount = 0;
    private boolean isTracking = false;

    private float lastMagnitude = 0;
    private static final float STEP_THRESHOLD = 10.5f;

    private static final float STRIDE_LENGTH = 0.78f;
    private static final int DAILY_GOAL = 6000;

    private DatabaseReference userStepHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TextViews
        stepText = findViewById(R.id.stepText);
        distanceText = findViewById(R.id.distanceText);
        ecoText = findViewById(R.id.ecoText);
        badgeText = findViewById(R.id.badgeText);

        // ProgressBar
        stepProgress = findViewById(R.id.stepProgress);
        stepProgress.setMax(DAILY_GOAL);

        // Buttons
        startBtn = findViewById(R.id.startBtn);
        resetBtn = findViewById(R.id.resetBtn);
        shareBtn = findViewById(R.id.shareBtn);
        historyBtn = findViewById(R.id.historyBtn);
        saveBtn = findViewById(R.id.saveBtn);

        // Firebase reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userStepHistoryRef = FirebaseDatabase.getInstance("https://project-group10-4546a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("stepHistory").child(userId);
        } else {
            Toast.makeText(this, "You must be logged in to save history", Toast.LENGTH_LONG).show();
            finish(); // Or redirect to login
            return;
        }

        // Sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Start tracking
        startBtn.setOnClickListener(v -> isTracking = true);

        // Reset steps
        resetBtn.setOnClickListener(v -> {
            stepCount = 0;
            updateUI();
        });

        // Share achievement (IMPLICIT INTENT)
        shareBtn.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            float distance = stepCount * STRIDE_LENGTH;
            float co2Saved = (distance / 1000) * 0.21f;

            String message = "🚶 I walked " + stepCount +
                    " steps today on campus and saved 🌱 " +
                    String.format("%.2f", co2Saved) +
                    " kg CO₂!";

            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // Go to history (EXPLICIT INTENT)
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StepTrackerActivity.this, StepHistoryActivity.class);
            startActivity(intent);
        });

// SAVE TO FIREBASE (DATABASE CREATE)
        saveBtn.setOnClickListener(v -> {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            float distance = stepCount * STRIDE_LENGTH;
            float co2Saved = (distance / 1000) * 0.21f;

            // Use convenience constructor
            StepRecord record = new StepRecord(
                    date,
                    stepCount,
                    distance,
                    co2Saved
            );

            userStepHistoryRef.child(date).setValue(record);
            Toast.makeText(this, "Steps saved successfully!", Toast.LENGTH_SHORT).show();
        });

        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isTracking) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = magnitude - lastMagnitude;
        lastMagnitude = magnitude;

        if (delta > STEP_THRESHOLD) {
            stepCount++;
            updateUI();
        }
    }

    private void updateUI() {
        stepText.setText(String.valueOf(stepCount));
        stepProgress.setProgress(stepCount);

        float distance = stepCount * STRIDE_LENGTH;
        distanceText.setText(String.format("%.2f meters", distance));

        float distanceKm = distance / 1000;
        float co2Saved = distanceKm * 0.21f;
        ecoText.setText(String.format("🌱 CO₂ Saved: %.2f kg", co2Saved));

        if (stepCount >= 6000) {
            badgeText.setText("🏆 Eco Champion (6000+ steps)");
        } else if (stepCount >= 3000) {
            badgeText.setText("🥈 Campus Explorer (3000+ steps)");
        } else if (stepCount >= 1000) {
            badgeText.setText("🥉 Beginner Walker (1000+ steps)");
        } else {
            badgeText.setText("🏅 Achievement: Not unlocked yet");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
