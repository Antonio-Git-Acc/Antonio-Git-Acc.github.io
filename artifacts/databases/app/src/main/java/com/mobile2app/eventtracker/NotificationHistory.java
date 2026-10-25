package com.mobile2app.eventtracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notification_history",
        foreignKeys = @ForeignKey(
                entity = Event.class,
                parentColumns = "id",
                childColumns = "event_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = { @Index("event_id") }
)
public class NotificationHistory {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    private long notificationId;

    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "sent_at")
    private long sentAt;

    @ColumnInfo(name = "notification_type")
    private String notificationType;   // "LOCAL" or "SMS"

    public NotificationHistory(int eventId, long sentAt, String notificationType) {
        this.eventId = eventId;
        this.sentAt = sentAt;
        this.notificationType = notificationType;
    }

    public long getNotificationId() { return notificationId; }
    public void setNotificationId(long notificationId) { this.notificationId = notificationId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public long getSentAt() { return sentAt; }
    public void setSentAt(long sentAt) { this.sentAt = sentAt; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
}