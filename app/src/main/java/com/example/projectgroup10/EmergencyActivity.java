package com.example.projectgroup10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    private static final String TAG = "EmergencyActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private Handler handler = new Handler();
    private Runnable sosRunnable;
    private boolean isSosTriggered = false;
    private boolean isEditMode = false;

    private DatabaseReference databaseReference;
    private RecyclerView rvContacts;
    private ContactAdapter adapter;
    private List<Contact> contactList;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        databaseReference = FirebaseDatabase.getInstance("https://project-group10-4546a-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("emergency_contacts");
        
        databaseReference.keepSynced(true);

        setupSosButton();
        setupEditButton();
        setupRecyclerView();
        setupFab();
        loadContacts();
    }

    private void setupSosButton() {
        Button btnSos = findViewById(R.id.btn_sos);

        sosRunnable = () -> {
            isSosTriggered = true;
            checkPermissionsAndSendSOS();
        };

        btnSos.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSosTriggered = false;
                    handler.postDelayed(sosRunnable, 3000);
                    Toast.makeText(this, "Hold for 3 seconds to trigger SOS", Toast.LENGTH_SHORT).show();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(sosRunnable);
                    if (!isSosTriggered) {
                        Toast.makeText(this, "SOS Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    return true;
            }
            return false;
        });
    }

    private void setupEditButton() {
        Button btnEditMode = findViewById(R.id.btn_edit_mode);
        btnEditMode.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            btnEditMode.setText(isEditMode ? "Done" : "Edit");
            if (adapter != null) {
                adapter.setEditMode(isEditMode);
            }
        });
    }

    private void checkPermissionsAndSendSOS() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            sendEmergencyAlertWithLocation();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void sendEmergencyAlertWithLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Fetching precise location...", Toast.LENGTH_SHORT).show();

        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
            .addOnSuccessListener(this, location -> {
                String message = "🚨 EMERGENCY SOS! I need help!";
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    message += "\nMy location: https://www.google.com/maps?q=" + latitude + "," + longitude;
                    Log.d(TAG, "Current Location: " + latitude + ", " + longitude);
                } else {
                    message += "\n(Location unavailable)";
                    Log.w(TAG, "Location is null even with getCurrentLocation");
                }
                
                sendSmsToContacts(message);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Location fetch failed", e);
                sendSmsToContacts("🚨 EMERGENCY SOS! I need help!\n(Location fetch failed)");
            });
    }

    private void sendSmsToContacts(String message) {
        if (contactList == null || contactList.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found!", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder allNumbers = new StringBuilder();
        for (int i = 0; i < contactList.size(); i++) {
            allNumbers.append(contactList.get(i).getPhoneNumber());
            if (i < contactList.size() - 1) {
                allNumbers.append(";");
            }
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + allNumbers.toString()));
            intent.putExtra("sms_body", message);
            intent.putExtra("address", allNumbers.toString());
            
            startActivity(intent);
            
            Toast.makeText(this, "Opening SMS app for all contacts...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to open SMS app", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                sendEmergencyAlertWithLocation();
            } else {
                Toast.makeText(this, "Permissions required for SOS.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupRecyclerView() {
        rvContacts = findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(contactList, new ContactAdapter.OnContactClickListener() {
            @Override
            public void onCallClick(Contact contact) {
                // New Call Logic
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
                startActivity(intent);
            }

            @Override
            public void onEditClick(Contact contact) {
                showContactDialog(contact);
            }

            @Override
            public void onDeleteClick(Contact contact) {
                deleteContact(contact);
            }
        });
        rvContacts.setAdapter(adapter);
    }

    private void setupFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_add_contact);
        fab.setOnClickListener(v -> showContactDialog(null));
    }

    private void loadContacts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Contact contact = postSnapshot.getValue(Contact.class);
                        if (contact != null) {
                            contactList.add(contact);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing contact", e);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmergencyActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showContactDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.et_contact_name);
        EditText etPhone = view.findViewById(R.id.et_contact_phone);

        if (contact != null) {
            etName.setText(contact.getName());
            etPhone.setText(contact.getPhoneNumber());
        }

        builder.setPositiveButton(contact == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                if (contact == null) {
                    addContact(name, phone);
                } else {
                    updateContact(contact.getId(), name, phone);
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void addContact(String name, String phone) {
        String id = databaseReference.push().getKey();
        Contact contact = new Contact(id, name, phone);
        if (id != null) {
            databaseReference.child(id).setValue(contact)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateContact(String id, String name, String phone) {
        Contact contact = new Contact(id, name, phone);
        databaseReference.child(id).setValue(contact)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show());
    }

    private void deleteContact(Contact contact) {
        databaseReference.child(contact.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show());
    }
}
