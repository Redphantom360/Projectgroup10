package com.example.projectgroup10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StepHistoryAdapter extends RecyclerView.Adapter<StepHistoryAdapter.ViewHolder> {

    private List<StepRecord> recordList;
    private Context context;
    private DatabaseReference databaseReference;

    public StepHistoryAdapter(Context context, List<StepRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("stepHistory");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_step_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepRecord record = recordList.get(position);

        holder.dateText.setText(record.date);
        holder.stepText.setText("Steps: " + record.steps);
        holder.distanceText.setText(String.format("Distance: %.2f m", record.distance));
        holder.co2Text.setText(String.format("CO₂ Saved: %.2f kg", record.co2));

        // 🔹 EDIT BUTTON
        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditStepActivity.class);
            intent.putExtra("id", record.date); // Using date as unique key
            intent.putExtra("steps", record.steps);
            context.startActivity(intent);
        });

        // 🔥 DELETE BUTTON
        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Record")
                    .setMessage("Are you sure you want to delete this record?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        databaseReference.child(record.date).removeValue();
                        Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateText, stepText, distanceText, co2Text;
        Button editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            stepText = itemView.findViewById(R.id.stepText);
            distanceText = itemView.findViewById(R.id.distanceText);
            co2Text = itemView.findViewById(R.id.co2Text);

            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
