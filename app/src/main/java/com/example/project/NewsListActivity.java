package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private FloatingActionButton btnAdd;

    private final ArrayList<Announcement> items = new ArrayList<>();
    private AnnouncementAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvNews = findViewById(R.id.rvNews);
        btnAdd = findViewById(R.id.btnAdd);

        // ✅ CLICK ITEM -> OPEN DETAIL SCREEN (NOT TOAST)
        adapter = new AnnouncementAdapter(items, a -> {
            Intent i = new Intent(NewsListActivity.this, NewsDetailActivity.class);
            i.putExtra("id", a.id);
            i.putExtra("title", a.title);
            i.putExtra("category", a.category);
            i.putExtra("content", a.content);
            startActivity(i);
        });

        rvNews.setLayoutManager(new LinearLayoutManager(this));
        rvNews.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // ✅ ADD BUTTON
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(NewsListActivity.this, AddAnnouncementActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    items.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        String id = doc.getId();
                        String title = doc.getString("title");
                        String category = doc.getString("category");
                        String content = doc.getString("content");
                        Long ts = doc.getLong("timestamp");

                        items.add(new Announcement(
                                id,
                                title == null ? "" : title,
                                category == null ? "" : category,
                                content == null ? "" : content,
                                ts == null ? 0L : ts
                        ));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
