package com.anderson.notepad.database.table;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "favorite_notes",
        primaryKeys = { "noteId" },
        foreignKeys = {
                @ForeignKey(
                        entity = Note.class,
                        parentColumns = { "id" },
                        childColumns = { "noteId" },
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Favorites {

    private int noteId;

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

}
