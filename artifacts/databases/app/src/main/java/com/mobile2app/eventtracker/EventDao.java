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

    // JOIN: Retrieve events with their category information
    @Query("SELECT e.*, c.name AS category_name, c.color AS category_color " +
            "FROM events e " +
            "LEFT JOIN categories c ON e.category_id = c.category_id " +
            "WHERE e.user_id = :userId " +
            "ORDER BY e.event_date ASC, e.event_time ASC")
    LiveData<List<EventWithCategory>> getEventsWithCategories(int userId);

    // Aggregate: Count events per category for a user
    @Query("SELECT c.name AS name, COUNT(e.id) AS event_count " +
            "FROM categories c " +
            "LEFT JOIN events e ON c.category_id = e.category_id AND e.user_id = :userId " +
            "GROUP BY c.category_id, c.name " +
            "ORDER BY event_count DESC")
    LiveData<List<CategoryStatistics>> getEventCountByCategory(int userId);

    // Subquery: Find events that have a recurring schedule
    @Query("SELECT * FROM events WHERE id IN " +
            "(SELECT event_id FROM recurring_events WHERE recurrence_pattern = :pattern)")
    List<Event> getRecurringEvents(String pattern);

    // Search
    @Query("SELECT * FROM events " +
            "WHERE user_id = :userId AND " +
            "(title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') " +
            "ORDER BY event_date ASC")
    List<Event> searchEvents(int userId, String searchQuery);
}