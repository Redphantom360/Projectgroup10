package com.example.projectgroup10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private OnContactClickListener listener;
    private boolean isEditMode = false;

    public interface OnContactClickListener {
        void onCallClick(Contact contact);
        void onEditClick(Contact contact);
        void onDeleteClick(Contact contact);
    }

    public ContactAdapter(List<Contact> contactList, OnContactClickListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhoneNumber());

        // Call button is always visible
        holder.btnCall.setOnClickListener(v -> listener.onCallClick(contact));

        // Edit/Delete buttons only visible in edit mode
        int editModeVisibility = isEditMode ? View.VISIBLE : View.GONE;
        holder.btnEdit.setVisibility(editModeVisibility);
        holder.btnDelete.setVisibility(editModeVisibility);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(contact));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        ImageButton btnCall, btnEdit, btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvPhone = itemView.findViewById(R.id.tv_contact_phone);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
