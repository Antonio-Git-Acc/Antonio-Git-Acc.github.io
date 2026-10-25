package com.mobile2app.eventtracker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class SmsPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private SwitchMaterial switchSms;
    private Button btnGrantPermission;
    private TextView tvStatus;
    private TextInputEditText etPhone;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        Toolbar toolbar = findViewById(R.id.toolbar_sms);
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        switchSms = findViewById(R.id.switch_sms);
        btnGrantPermission = findViewById(R.id.btn_grant_permission);
        tvStatus = findViewById(R.id.tv_status);
        etPhone = findViewById(R.id.et_phone);

        // Load saved preferences
        switchSms.setChecked(prefs.getBoolean("sms_enabled", false));
        etPhone.setText(prefs.getString("phone_number", ""));

        // Switch toggled
        switchSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save phone number immediately
            prefs.edit()
                    .putString("phone_number", etPhone.getText().toString().trim())
                    .putBoolean("sms_enabled", isChecked)
                    .apply();

            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                } else {
                    tvStatus.setText("SMS reminders enabled");
                }
            } else {
                tvStatus.setText("SMS reminders disabled");
            }
        });

        // Manual grant button
        btnGrantPermission.setOnClickListener(v -> {
            // Save phone number before requesting permission
            prefs.edit().putString("phone_number", etPhone.getText().toString().trim()).apply();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        });

        // Save phone number when focus leaves the field
        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                prefs.edit().putString("phone_number", etPhone.getText().toString().trim()).apply();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tvStatus.setText("Permission granted – SMS reminders enabled");
                switchSms.setChecked(true);
            } else {
                tvStatus.setText("Permission denied – SMS reminders disabled");
                switchSms.setChecked(false);
                prefs.edit().putBoolean("sms_enabled", false).apply();
            }
        }
    }
}