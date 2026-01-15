package com.example.projectgroup10;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StepHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StepHistoryAdapter adapter;
    private ArrayList<StepRecord> stepList;

    private DatabaseReference userStepHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stepList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userStepHistoryRef = FirebaseDatabase.getInstance("https://project-group10-4546a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("stepHistory").child(userId);

            adapter = new StepHistoryAdapter(this, stepList, userStepHistoryRef);
            recyclerView.setAdapter(adapter);

            loadStepHistory();
        } else {
            Toast.makeText(this, "You need to be logged in to view history", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadStepHistory() {
        userStepHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StepRecord record = dataSnapshot.getValue(StepRecord.class);
                    stepList.add(record);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StepHistoryActivity.this, "Failed to load history.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
