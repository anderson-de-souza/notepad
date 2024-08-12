package com.anderson.notepad.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.anderson.notepad.database.dao.NoteDAO;
import com.anderson.notepad.database.table.Favorites;
import com.anderson.notepad.database.table.Locks;
import com.anderson.notepad.database.table.Note;

@Database(
        entities = {
                Note.class,
                Favorites.class,
                Locks.class
        },
        version = 1,
        exportSchema = true
)
public abstract class RealDatabase extends RoomDatabase {

    private static RealDatabase instance;

    public static synchronized RealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, RealDatabase.class, "notepad.sql")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract NoteDAO noteDAO();

}
