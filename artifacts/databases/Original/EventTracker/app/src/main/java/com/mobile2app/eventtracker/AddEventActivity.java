package com.mobile2app.eventtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.util.Date;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription;
    private Button btnDate, btnTime, btnSave;
    private String selectedDate = "", selectedTime = "";
    private AddEventViewModel viewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("logged_in_user_id", -1);

        Toolbar toolbar = findViewById(R.id.toolbar_add);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        btnDate = findViewById(R.id.btn_date);
        btnTime = findViewById(R.id.btn_time);
        btnSave = findViewById(R.id.btn_save);

        viewModel = new ViewModelProvider(this).get(AddEventViewModel.class);

        // Date picker
        btnDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select event date")
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                // Extract the UTC date components exactly as selected
                java.util.Calendar utcCal = java.util.Calendar.getInstance(
                        java.util.TimeZone.getTimeZone("UTC"));
                utcCal.setTimeInMillis(selection);
                int year = utcCal.get(java.util.Calendar.YEAR);
                int month = utcCal.get(java.util.Calendar.MONTH) + 1;
                int day = utcCal.get(java.util.Calendar.DAY_OF_MONTH);

                // Store as yyyy-MM-dd
                selectedDate = String.format(Locale.getDefault(),
                        "%04d-%02d-%02d", year, month, day);

                // Display friendly date using UTC to avoid shift
                SimpleDateFormat displayFormat = new SimpleDateFormat(
                        "EEE, MMM d, yyyy", Locale.getDefault());
                displayFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                btnDate.setText(displayFormat.format(new Date(selection)));
            });
            picker.show(getSupportFragmentManager(), "date_picker");
        });

        // Time picker
        btnTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setTitleText("Select event time")
                    .build();
            picker.addOnPositiveButtonClickListener(dialog -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                btnTime.setText(selectedTime);
            });
            picker.show(getSupportFragmentManager(), "time_picker");
        });

        // Save event
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (title.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Title, date, and time are required", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveEvent(userId, title, desc, selectedDate, selectedTime);
            scheduleAlarm(selectedDate, selectedTime, title);
            Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void scheduleAlarm(String date, String time, String eventTitle) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, EventAlarmReceiver.class);
        intent.putExtra("event_title", eventTitle);
        int requestCode = (date + time + eventTitle).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");
        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        long triggerTime = calendar.getTimeInMillis();
        if (triggerTime > System.currentTimeMillis()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}