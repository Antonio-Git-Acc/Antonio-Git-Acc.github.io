package com.mobile2app.eventtracker;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

    /** Result of an authentication or registration attempt. */
    public static class AuthResult {
        public enum Type {
            LOGIN_SUCCESS,
            LOGIN_FAILURE,
            REGISTER_SUCCESS,
            REGISTER_FAILURE
        }

        public final Type type;
        public final User user; // non-null only on LOGIN_SUCCESS

        public AuthResult(Type type, User user) {
            this.type = type;
            this.user = user;
        }
    }

    private final AppRepository repository;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();

    public LoginViewModel(Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    /** Attempt to log in. Result is posted to getAuthResult(). */
    public void login(String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = repository.findUserByUsername(username);
            if (user == null ||
                    !HashUtils.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                authResult.postValue(new AuthResult(AuthResult.Type.LOGIN_FAILURE, null));
            } else {
                authResult.postValue(new AuthResult(AuthResult.Type.LOGIN_SUCCESS, user));
            }
        });
    }

    /** Attempt to register a new account. Result is posted to getAuthResult(). */
    public void register(String username, String password, String phone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User existing = repository.findUserByUsername(username);
            if (existing != null) {
                authResult.postValue(new AuthResult(AuthResult.Type.REGISTER_FAILURE, null));
                return;
            }

            String salt = HashUtils.generateSalt();
            String hashed = HashUtils.hashPassword(password, salt);
            User newUser = new User(username, hashed, salt, phone);
            repository.insertUser(newUser);
            authResult.postValue(new AuthResult(AuthResult.Type.REGISTER_SUCCESS, null));
        });
    }
}