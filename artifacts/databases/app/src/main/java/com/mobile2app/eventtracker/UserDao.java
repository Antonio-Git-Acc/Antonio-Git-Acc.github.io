package com.mobile2app.eventtracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    // Used only by DebugActivity for CS 499 verification. Safe to remove in production.
    @Query("SELECT * FROM users LIMIT 1")
    User getAnyUser();

    // Used only by DebugActivity for CS 499 verification. Safe to remove in production.
    @Insert
    long insertAndReturnId(User user);
}

