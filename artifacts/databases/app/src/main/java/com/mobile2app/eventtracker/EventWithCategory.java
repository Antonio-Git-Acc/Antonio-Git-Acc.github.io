package com.mobile2app.eventtracker;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

/**
 * Result type for queries that JOIN the events and categories tables.
 */
public class EventWithCategory {

    @Embedded
    public Event event;

    @ColumnInfo(name = "category_name")
    public String categoryName;

    @ColumnInfo(name = "category_color")
    public String categoryColor;
}