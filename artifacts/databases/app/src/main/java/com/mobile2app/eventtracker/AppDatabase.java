package com.mobile2app.eventtracker;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class,
                Event.class,
                Category.class,
                RecurringEvent.class,
                NotificationHistory.class
        },
        version = 3,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public abstract UserDao userDao();
    public abstract EventDao eventDao();
    public abstract CategoryDao categoryDao();
    public abstract RecurringEventDao recurringEventDao();
    public abstract NotificationHistoryDao notificationHistoryDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // 1. Load the SQLCipher native library
                    System.loadLibrary("sqlcipher");

                    // 2. Get or create the database passphrase
                    DatabaseKeyManager keyManager = new DatabaseKeyManager(context);
                    byte[] passphrase = keyManager.getOrCreatePassphrase();

                    // 3. Create the SQLCipher factory
                    SupportOpenHelperFactory factory = new SupportOpenHelperFactory(passphrase);

                    // 4. Build the database with the factory
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "event_tracker_db")
                            .openHelperFactory(factory) // Apply encryption
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migration from version 1 to 2: Add Category table and category_id to Event
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `categories` (" +
                            "`category_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`color` TEXT NOT NULL DEFAULT '#2196F3')"
            );
            database.execSQL(
                    "ALTER TABLE `events` ADD COLUMN `category_id` INTEGER " +
                            "REFERENCES `categories`(`category_id`) ON DELETE SET NULL"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_events_category_id` ON `events`(`category_id`)"
            );
            database.execSQL(
                    "ALTER TABLE `users` ADD COLUMN `salt` TEXT"
            );
        }
    };

    // Migration from version 2 to 3: Add RecurringEvent and NotificationHistory tables
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `recurring_events` (" +
                            "`recurring_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`event_id` INTEGER NOT NULL, " +
                            "`recurrence_pattern` TEXT NOT NULL, " +
                            "`interval` INTEGER NOT NULL DEFAULT 1, " +
                            "`end_date` TEXT, " +
                            "FOREIGN KEY(`event_id`) REFERENCES `events`(`event_id`) ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_recurring_events_event_id` " +
                            "ON `recurring_events`(`event_id`)"
            );
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `notification_history` (" +
                            "`notification_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`event_id` INTEGER NOT NULL, " +
                            "`sent_at` INTEGER NOT NULL, " +
                            "`notification_type` TEXT NOT NULL, " +
                            "FOREIGN KEY(`event_id`) REFERENCES `events`(`event_id`) ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_notification_history_event_id` " +
                            "ON `notification_history`(`event_id`)"
            );
        }
    };
}