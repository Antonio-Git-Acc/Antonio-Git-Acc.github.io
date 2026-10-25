/**
 * Debug-only activity used during CS 499 Milestone Four to verify
 * database enhancements. Not part of the user-facing application.
 * See the Milestone Four narrative for the verification evidence.
 */

package com.mobile2app.eventtracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "CS499_DEBUG";
    private static final String TEST_PASSWORD = "password123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Priority 3: Unique salts
        logSaltDemo();

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        // Set up LiveData observers on the main thread
        db.categoryDao().getAllCategories().observe(this, cats -> {
            Log.i(TAG, "Categories in DB: " + (cats != null ? cats.size() : 0));
        });

        // Do inserts and cascade test on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Log.i(TAG, "=== Priority 4: New DAOs ===");
            db.categoryDao().insert(new Category("Work", "#FF6F00"));
            db.categoryDao().insert(new Category("Personal", "#1976D2"));
            Log.i(TAG, "Inserted 2 categories");

            runCascadeTest(db);
        });
    }

    private void logSaltDemo() {
        Log.i(TAG, "=== Priority 3: Unique Salts ===");
        String saltA = HashUtils.generateSalt();
        String saltB = HashUtils.generateSalt();
        String hashA = HashUtils.hashPassword(TEST_PASSWORD, saltA);
        String hashB = HashUtils.hashPassword(TEST_PASSWORD, saltB);
        Log.i(TAG, "Salt A: " + saltA);
        Log.i(TAG, "Hash A: " + hashA);
        Log.i(TAG, "Salt B: " + saltB);
        Log.i(TAG, "Hash B: " + hashB);
        Log.i(TAG, "Salts differ: " + !saltA.equals(saltB));
        Log.i(TAG, "Hashes differ (same password): " + !hashA.equals(hashB));
    }

    private void runCascadeTest(AppDatabase db) {
        Log.i(TAG, "=== Priority 5: Cascade Delete ===");
        try {
            // Find a user, or create a test user if none exists
            User anyUser = db.userDao().getAnyUser();
            int userId;
            if (anyUser == null) {
                Log.i(TAG, "No users found; creating a temporary test user");
                String salt = HashUtils.generateSalt();
                String hash = HashUtils.hashPassword("cascadeTest", salt);
                User testUser = new User("__cascade_test__", hash, salt, "");
                long newId = db.userDao().insertAndReturnId(testUser);
                userId = (int) newId;
                Log.i(TAG, "Created test user with id = " + userId);
            } else {
                userId = anyUser.getId();
                Log.i(TAG, "Using existing userId = " + userId);
            }

            Event tempEvent = new Event(userId, "Cascade Test Event",
                    "Temporary event for cascade verification",
                    "2099-12-31", "23:59");
            db.eventDao().insert(tempEvent);
            Log.i(TAG, "Inserted temp event");

            List<Event> events = db.eventDao().searchEvents(userId, "Cascade Test Event");
            if (events != null && !events.isEmpty()) {
                int tempId = events.get(0).getId();
                db.notificationHistoryDao().insert(
                        new NotificationHistory(tempId, System.currentTimeMillis(), "LOCAL"));

                int before = db.notificationHistoryDao()
                        .getNotificationHistoryForEvent(tempId).size();
                Log.i(TAG, "Notification history rows before delete: " + before);

                db.eventDao().deleteById(tempId);

                int after = db.notificationHistoryDao()
                        .getNotificationHistoryForEvent(tempId).size();
                Log.i(TAG, "Notification history rows after delete: " + after);
                Log.i(TAG, "Cascade worked: " + (after == 0));
            } else {
                Log.w(TAG, "Could not find inserted temp event");
            }
        } catch (Exception e) {
            Log.e(TAG, "Cascade test failed", e);
        }
        Log.i(TAG, "=== Debug activity complete ===");
    }
}