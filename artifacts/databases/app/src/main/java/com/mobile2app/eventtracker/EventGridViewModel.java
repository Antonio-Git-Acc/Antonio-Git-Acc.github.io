package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class EventGridViewModel extends AndroidViewModel {
    private AppRepository repository;
    private int userId;

    public EventGridViewModel(Application application, int userId) {
        super(application);
        repository = new AppRepository(application);
        this.userId = userId;
    }

    public LiveData<List<Event>> getEvents() {
        return repository.getAllEventsForUser(userId);
    }

    public void deleteEvent(Event event) {
        repository.deleteEvent(event);
    }
}