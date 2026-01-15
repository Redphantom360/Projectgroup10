package com.example.projectgroup10;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditStepActivity extends AppCompatActivity {

    private EditText editSteps;
    private Button saveBtn;

    private String recordId;
    private DatabaseReference userStepHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_step);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editSteps = findViewById(R.id.editSteps);
        saveBtn = findViewById(R.id.saveBtn);

        recordId = getIntent().getStringExtra("id");
        int steps = getIntent().getIntExtra("steps", 0);

        editSteps.setText(String.valueOf(steps));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userStepHistoryRef = FirebaseDatabase.getInstance("https://project-group10-4546a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("stepHistory").child(userId);
        } else {
            Toast.makeText(this, "You need to be logged in to edit history", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        saveBtn.setOnClickListener(v -> {
            int newSteps = Integer.parseInt(editSteps.getText().toString());

            float distance = newSteps * 0.78f;
            float co2Saved = (distance / 1000) * 0.21f;

            userStepHistoryRef.child(recordId).child("steps").setValue(newSteps);
            userStepHistoryRef.child(recordId).child("distance").setValue(distance);
            userStepHistoryRef.child(recordId).child("co2Saved").setValue(co2Saved);

            Toast.makeText(this, "Record updated", Toast.LENGTH_SHORT).show();
            finish();
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
