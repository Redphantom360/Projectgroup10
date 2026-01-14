package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvCategory, tvContent;
    private Button btnEdit, btnDelete, btnShare;

    private String id, title, category, content;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Bind views
        tvTitle = findViewById(R.id.tvTitle);
        tvCategory = findViewById(R.id.tvCategory);
        tvContent = findViewById(R.id.tvContent);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);

        db = FirebaseFirestore.getInstance();

        // Get data from intent
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        category = getIntent().getStringExtra("category");
        content = getIntent().getStringExtra("content");

        // Prevent null crashes
        if (title == null) title = "";
        if (category == null) category = "";
        if (content == null) content = "";

        // Display data
        tvTitle.setText(title);
        tvCategory.setText(category);
        tvContent.setText(content);

        // EDIT button
        btnEdit.setOnClickListener(v -> {
            if (id == null || id.isEmpty()) {
                Toast.makeText(this, "Invalid document ID", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(NewsDetailActivity.this, AddAnnouncementActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("category", category);
            intent.putExtra("content", content);
            startActivity(intent);
        });

        // DELETE button
        btnDelete.setOnClickListener(v -> confirmDelete());

        // SHARE button
        btnShare.setOnClickListener(v -> shareAnnouncement());
    }

    private void confirmDelete() {
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Invalid document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete this announcement?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAnnouncement())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAnnouncement() {
        db.collection("announcements").document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Announcement deleted", Toast.LENGTH_SHORT).show();
                    finish(); // back to list
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void shareAnnouncement() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Campus Announcement");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + content);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
