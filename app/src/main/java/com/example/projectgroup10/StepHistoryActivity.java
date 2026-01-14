package com.example.projectgroup10;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        recyclerView = findViewById(R.id.recyclerViewSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stepList = new ArrayList<>();
        adapter = new StepHistoryAdapter(this, stepList); // ✅ FIXED
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("stepHistory");

        databaseReference.addValueEventListener(new ValueEventListener() {
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
                // Not used
            }
        });
    }
}
