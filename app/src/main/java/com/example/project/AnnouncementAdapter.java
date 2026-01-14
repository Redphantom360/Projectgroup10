package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.VH> {

    public interface OnItemClick {
        void onClick(Announcement a);
    }

    private final List<Announcement> list;
    private final OnItemClick listener;

    public AnnouncementAdapter(List<Announcement> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Announcement a = list.get(position);
        holder.tvTitle.setText(a.title);
        holder.tvCategory.setText(a.category);

        holder.itemView.setOnClickListener(v -> listener.onClick(a));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
