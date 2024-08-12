package com.anderson.notepad.database.table;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.anderson.notepad.R;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "notes")
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;

    private String creationTimestamp;
    private String lastEditionTimestamp;

    public Note() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getLastEditionTimestamp() {
        return lastEditionTimestamp;
    }

    public void setLastEditionTimestamp(String lastEditionTimestamp) {
        this.lastEditionTimestamp = lastEditionTimestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof  Note)) {
            return false;
        }
        return id == ((Note) obj).getId();
    }

    private Note(@NonNull Parcel source) {
        id = source.readInt();
        title = source.readString();
        description = source.readString();
        creationTimestamp = source.readString();
        lastEditionTimestamp = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(creationTimestamp);
        dest.writeString(lastEditionTimestamp);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }

    };

}
