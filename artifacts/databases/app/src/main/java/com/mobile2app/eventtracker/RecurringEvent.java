package com.mobile2app.eventtracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "recurring_events",
        foreignKeys = @ForeignKey(
                entity = Event.class,
                parentColumns = "id",
                childColumns = "event_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = { @Index("event_id") }
)
public class RecurringEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recurring_id")
    private long recurringId;

    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "recurrence_pattern")
    private String recurrencePattern;   // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"

    @ColumnInfo(name = "interval")
    private int interval;

    @ColumnInfo(name = "end_date")
    private String endDate;             // "yyyy-MM-dd" or null

    public RecurringEvent(int eventId, String recurrencePattern, int interval, String endDate) {
        this.eventId = eventId;
        this.recurrencePattern = recurrencePattern;
        this.interval = interval;
        this.endDate = endDate;
    }

    public long getRecurringId() { return recurringId; }
    public void setRecurringId(long recurringId) { this.recurringId = recurringId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}