package com.mobile2app.eventtracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

public class LoginViewModel extends AndroidViewModel {
    private AppRepository repository;

    public LoginViewModel(Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    // Returns true if credentials match, false otherwise
    public boolean authenticate(String username, String password) {
        User user = repository.findUserByUsername(username);
        if (user == null) return false;
        String hashedInput = HashUtils.hashPassword(password, "staticSalt");
        return hashedInput.equals(user.getPasswordHash());
    }

    // Returns true if registration succeeded, false if username already exists
    public boolean register(String username, String password, String phone) {
        User existing = repository.findUserByUsername(username);
        if (existing != null) return false;

        String hashed = HashUtils.hashPassword(password, "staticSalt");
        User newUser = new User(username, hashed, phone);
        repository.insertUser(newUser);
        return true;
    }

    public User getUserByUsername(String username) {
        return repository.findUserByUsername(username);
    }
}