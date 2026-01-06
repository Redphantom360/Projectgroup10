package com.example.projectgroup10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    // 1. Get the main database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://project-group10-4546a-default-rtdb.asia-southeast1.firebasedatabase.app");

    // 2. Create references for your 4 "Tables" (Nodes)
    DatabaseReference rootRef = database.getReference();
    DatabaseReference emergencyRef = database.getReference("emergencies");
    DatabaseReference lostFoundRef = database.getReference("lost_found");
    DatabaseReference stepCounterRef = database.getReference("steps");
    DatabaseReference newsRef = database.getReference("campus_news");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initializeDatabaseStructure();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
    }

    private void initializeDatabaseStructure() {
        // This adds placeholder data so you can see the "tables" in your Firebase Console
        emergencyRef.child("info").setValue("Emergency contacts node initialized");
        lostFoundRef.child("info").setValue("Lost and Found items node initialized");
        stepCounterRef.child("info").setValue("Step counter data node initialized");
        newsRef.child("info").setValue("Campus news node initialized");
    }

    private void setupClickListeners() {
        MaterialCardView cardEmergency = findViewById(R.id.card_emergency);
        MaterialCardView cardLostFound = findViewById(R.id.card_lost_found);
        MaterialCardView cardStepCounter = findViewById(R.id.card_step_counter);
        MaterialCardView cardNews = findViewById(R.id.card_news);

        cardEmergency.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
            startActivity(intent);
        });

        cardLostFound.setOnClickListener(v ->
                Toast.makeText(this, "Lost & Found clicked", Toast.LENGTH_SHORT).show()
        );

        cardStepCounter.setOnClickListener(v ->
                Toast.makeText(this, "Step Counter clicked", Toast.LENGTH_SHORT).show()
        );

        cardNews.setOnClickListener(v ->
                Toast.makeText(this, "Campus News clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
