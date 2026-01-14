package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAnnouncementActivity extends AppCompatActivity {

    private EditText etTitle, etCategory, etContent;
    private Button btnSave;
    private FirebaseFirestore db;

    // ✅ Firestore docId. If not null => EDIT mode
    private String editId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        etTitle = findViewById(R.id.etTitle);
        etCategory = findViewById(R.id.etCategory);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        // ✅ get extras from intent
        editId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String category = getIntent().getStringExtra("category");
        String content = getIntent().getStringExtra("content");

        if (editId != null && !editId.isEmpty()) {
            setTitle("Edit Announcement");
            btnSave.setText("Update Announcement");

            if (title != null) etTitle.setText(title);
            if (category != null) etCategory.setText(category);
            if (content != null) etContent.setText(content);
        } else {
            setTitle("Add Announcement");
            btnSave.setText("Save Announcement");
        }

        btnSave.setOnClickListener(v -> saveOrUpdate());
    }

    private void saveOrUpdate() {
        String title = etTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("category", category);
        data.put("content", content);
        data.put("timestamp", System.currentTimeMillis()); // ✅ IMPORTANT for list sorting

        // ✅ EDIT => update same document
        if (editId != null && !editId.isEmpty()) {
            db.collection("announcements").document(editId)
                    .update(data)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }
        // ✅ ADD => create NEW document (Firestore auto id)
        else {
            db.collection("announcements")
                    .add(data)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Add failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }
    }
}
