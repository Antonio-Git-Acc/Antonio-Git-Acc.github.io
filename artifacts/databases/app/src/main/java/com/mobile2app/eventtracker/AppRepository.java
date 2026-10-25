package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AppRepository {
    private UserDao userDao;
    private EventDao eventDao;
    private CategoryDao categoryDao;
    private RecurringEventDao recurringEventDao;
    private NotificationHistoryDao notificationHistoryDao;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        eventDao = db.eventDao();
        categoryDao = db.categoryDao();
        recurringEventDao = db.recurringEventDao();
        notificationHistoryDao = db.notificationHistoryDao();
    }

    // --- User operations ---
    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public User findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    // --- Event operations ---
    public void insertEvent(Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> eventDao.insert(event));
    }

    public void updateEvent(Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> eventDao.update(event));
    }

    public void deleteEvent(Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> eventDao.delete(event));
    }

    public void deleteEventById(int eventId) {
        AppDatabase.databaseWriteExecutor.execute(() -> eventDao.deleteById(eventId));
    }

    public LiveData<List<Event>> getAllEventsForUser(int userId) {
        return eventDao.getAllEventsForUser(userId);
    }

    public LiveData<List<EventWithCategory>> getEventsWithCategories(int userId) {
        return eventDao.getEventsWithCategories(userId);
    }

    // --- Category operations ---
    public void insertCategory(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.insert(category));
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    // --- Recurring event operations ---
    public void insertRecurringEvent(RecurringEvent recurringEvent) {
        AppDatabase.databaseWriteExecutor.execute(() -> recurringEventDao.insert(recurringEvent));
    }

    public RecurringEvent getRecurringEventForEvent(int eventId) {
        return recurringEventDao.getRecurringEventForEvent(eventId);
    }

    // --- Notification history operations ---
    public void insertNotificationHistory(NotificationHistory notification) {
        AppDatabase.databaseWriteExecutor.execute(() -> notificationHistoryDao.insert(notification));
    }

    public List<NotificationHistory> getNotificationHistoryForEvent(int eventId) {
        return notificationHistoryDao.getNotificationHistoryForEvent(eventId);
    }
}