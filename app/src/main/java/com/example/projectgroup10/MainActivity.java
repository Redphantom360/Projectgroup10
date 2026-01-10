package com.example.projectgroup10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
    }

    private void setupClickListeners() {
        MaterialCardView cardEmergency = findViewById(R.id.card_emergency);
        MaterialCardView cardLostFound = findViewById(R.id.card_lost_found);
        MaterialCardView cardStepCounter = findViewById(R.id.card_step_counter);
        MaterialCardView cardNews = findViewById(R.id.card_news);
        Button btnLogout = findViewById(R.id.btn_logout);

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

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
