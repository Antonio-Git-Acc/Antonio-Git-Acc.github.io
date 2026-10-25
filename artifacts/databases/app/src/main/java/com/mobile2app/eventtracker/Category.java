package com.mobile2app.eventtracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "color")
    private String color;

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}