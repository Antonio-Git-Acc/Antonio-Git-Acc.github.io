package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AppRepository {
    private UserDao userDao;
    private EventDao eventDao;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        eventDao = db.eventDao();
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
}