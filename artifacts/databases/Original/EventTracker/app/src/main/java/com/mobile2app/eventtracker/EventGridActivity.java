package com.mobile2app.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventGridActivity extends AppCompatActivity {

    private EventGridViewModel viewModel;
    private EventAdapter adapter;
    private TextView tvEmpty;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_grid);

        // Get logged-in user ID from SharedPreferences
        userId = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getInt("logged_in_user_id", -1);
        if (userId == -1) {
            // Not logged in – go back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Empty placeholder
        tvEmpty = findViewById(R.id.tv_empty);

        // RecyclerView setup
        RecyclerView rvEvents = findViewById(R.id.rv_events);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(event -> viewModel.deleteEvent(event));
        rvEvents.setAdapter(adapter);

        // ViewModel with factory
        viewModel = new ViewModelProvider(this,
                new EventGridViewModelFactory(getApplication(), userId))
                .get(EventGridViewModel.class);

        // Observe events
        viewModel.getEvents().observe(this, events -> {
            adapter.setEvents(events);
            if (events == null || events.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        });

        // FAB → Add Event screen
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEventActivity.class)));

        createNotificationChannel();
    }


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return; // channel will be created after permission granted
            }
        }
        // Actually create the channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Event Reminders";
            String description = "Notifications for upcoming events";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "event_reminders", name, importance);
            channel.setDescription(description);
            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0
                && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Permission granted, create the channel now
            createNotificationChannel();
        }
    }

    // Inflate toolbar menu (settings gear)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_grid, menu);
        return true;
    }

    // Handle settings menu click → SMS permission screen
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SmsPermissionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}