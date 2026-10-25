package com.mobile2app.eventtracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecurringEventDao {

    @Insert
    void insert(RecurringEvent recurringEvent);

    @Delete
    void delete(RecurringEvent recurringEvent);

    @Query("SELECT * FROM recurring_events WHERE event_id = :eventId LIMIT 1")
    RecurringEvent getRecurringEventForEvent(int eventId);

    @Query("SELECT * FROM recurring_events WHERE recurrence_pattern = :pattern")
    List<RecurringEvent> getRecurringEventsByPattern(String pattern);

    @Query("SELECT * FROM recurring_events")
    List<RecurringEvent> getAllRecurringEvents();
}