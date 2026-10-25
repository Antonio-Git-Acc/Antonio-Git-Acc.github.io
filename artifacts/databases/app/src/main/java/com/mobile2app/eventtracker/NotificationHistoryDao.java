package com.mobile2app.eventtracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationHistoryDao {

    @Insert
    void insert(NotificationHistory notification);

    @Query("SELECT * FROM notification_history WHERE event_id = :eventId ORDER BY sent_at DESC")
    List<NotificationHistory> getNotificationHistoryForEvent(int eventId);

    @Query("SELECT * FROM notification_history ORDER BY sent_at DESC LIMIT :limit")
    List<NotificationHistory> getRecentNotifications(int limit);
}