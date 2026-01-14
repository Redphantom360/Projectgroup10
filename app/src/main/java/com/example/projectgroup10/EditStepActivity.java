package com.example.projectgroup10;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditStepActivity extends AppCompatActivity {

    private EditText editSteps;
    private Button saveBtn;

    private String recordId;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_step);

        editSteps = findViewById(R.id.editSteps);
        saveBtn = findViewById(R.id.saveBtn);

        recordId = getIntent().getStringExtra("id");
        int steps = getIntent().getIntExtra("steps", 0);

        editSteps.setText(String.valueOf(steps));

        dbRef = FirebaseDatabase.getInstance()
                .getReference("stepHistory");

        saveBtn.setOnClickListener(v -> {
            int newSteps = Integer.parseInt(editSteps.getText().toString());

            float distance = newSteps * 0.78f;
            float co2 = (distance / 1000) * 0.21f;

            dbRef.child(recordId).child("steps").setValue(newSteps);
            dbRef.child(recordId).child("distance").setValue(distance);
            dbRef.child(recordId).child("co2").setValue(co2);

            Toast.makeText(this, "Record updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
