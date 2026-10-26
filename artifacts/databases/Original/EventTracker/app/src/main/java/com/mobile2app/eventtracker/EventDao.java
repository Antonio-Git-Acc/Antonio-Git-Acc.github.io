package com.mobile2app.eventtracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteById(int eventId);

    @Query("SELECT * FROM events WHERE user_id = :userId ORDER BY event_date ASC, event_time ASC")
    LiveData<List<Event>> getAllEventsForUser(int userId);
}