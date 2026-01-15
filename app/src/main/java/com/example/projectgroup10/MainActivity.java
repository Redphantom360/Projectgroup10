package com.example.projectgroup10;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView rvUpcomingEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton btnOpenDrawer = findViewById(R.id.btn_open_drawer);
        btnOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        updateWelcomeMessage();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        setupCardClickListeners();
        setupUpcomingEvents();

        // Set up the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If the callback is enabled, this is the default behavior.
                    // For this activity, the default is to finish.
                    finish();
                }
            }
        });
    }

    private void updateWelcomeMessage() {
        TextView tvWelcome = findViewById(R.id.tv_welcome_user);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    if (username != null) {
                        tvWelcome.setText("Welcome, " + username + "!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Fallback to email if username can't be fetched
                    if(user.getEmail() != null) {
                        String name = user.getEmail().split("@")[0];
                        tvWelcome.setText("Welcome, " + name + "!");
                    }
                }
            });
        }
    }

    private void setupCardClickListeners() {
        MaterialCardView cardNews = findViewById(R.id.card_news);
        MaterialCardView cardStepCounter = findViewById(R.id.card_step_counter);
        MaterialCardView cardLostFound = findViewById(R.id.card_lost_found);
        MaterialCardView cardEmergency = findViewById(R.id.card_emergency);

        cardNews.setOnClickListener(v -> Toast.makeText(this, "News clicked", Toast.LENGTH_SHORT).show());
        cardStepCounter.setOnClickListener(v -> startActivity(new Intent(this, StepTrackerActivity.class)));
        cardLostFound.setOnClickListener(v -> Toast.makeText(this, "Lost & Found clicked", Toast.LENGTH_SHORT).show());
        cardEmergency.setOnClickListener(v -> startActivity(new Intent(this, EmergencyActivity.class)));
    }

    private void setupUpcomingEvents() {
        rvUpcomingEvents = findViewById(R.id.rv_upcoming_events);
        rvUpcomingEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        eventList = new ArrayList<>();
        // Add sample data
        eventList.add(new Event("Campus Orientation", "Auditorium", R.drawable.ic_launcher_background));
        eventList.add(new Event("Tech Innovation Seminar", "Tech Hall", R.drawable.ic_launcher_background));
        eventList.add(new Event("Music Fest 2024", "Main Field", R.drawable.ic_launcher_background));

        eventAdapter = new EventAdapter(eventList);
        rvUpcomingEvents.setAdapter(eventAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_news) {
            Toast.makeText(this, "News & Announcement clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_step_counter) {
            startActivity(new Intent(this, StepTrackerActivity.class));
        } else if (itemId == R.id.nav_lost_found) {
            Toast.makeText(this, "Lost and Found clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_safety_emergency) {
            startActivity(new Intent(this, EmergencyActivity.class));
        } else if (itemId == R.id.nav_about) {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = 
        item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_events) {
                Toast.makeText(MainActivity.this, "Events clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_messages) {
                Toast.makeText(MainActivity.this, "Messages clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        };
}
