package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EventGridViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int userId;

    public EventGridViewModelFactory(Application application, int userId) {
        this.application = application;
        this.userId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventGridViewModel.class)) {
            return (T) new EventGridViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}