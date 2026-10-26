package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

public class AddEventViewModel extends AndroidViewModel {
    private AppRepository repository;

    public AddEventViewModel(Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void saveEvent(int userId, String title, String description, String date, String time) {
        Event event = new Event(userId, title, description, date, time);
        repository.insertEvent(event);
    }
}