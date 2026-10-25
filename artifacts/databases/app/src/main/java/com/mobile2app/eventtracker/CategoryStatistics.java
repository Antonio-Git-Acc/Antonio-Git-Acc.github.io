package com.mobile2app.eventtracker;

import androidx.room.ColumnInfo;

/**
 * Result type for aggregate queries that count events per category.
 */
public class CategoryStatistics {

    @ColumnInfo(name = "name")
    public String categoryName;

    @ColumnInfo(name = "event_count")
    public int eventCount;
}