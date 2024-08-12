package com.anderson.notepad.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.anderson.notepad.database.table.Note;

import java.util.List;

@Dao
public interface NoteDAO {

    @Query("INSERT INTO notes(title, description, creationTimestamp, lastEditionTimestamp) VALUES (:title, :description, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    void insert(String title, String description);

    @Query("UPDATE notes SET title = :title, description = :description, lastEditionTimestamp = CURRENT_TIMESTAMP WHERE id = :id")
    void update(int id, String title, String description);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM notes")
    void deleteAll();

    @Query("DELETE FROM notes WHERE id in (:ids)")
    void deleteAll(int ...ids);

    @Query("SELECT * FROM notes WHERE id = :id")
    Note getNoteById(int id);

    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getAllNotes();

    @Query("INSERT OR IGNORE INTO favorite_notes VALUES(:noteId)")
    void insertIntoFavoriteNotes(int noteId);

    @Query("DELETE FROM favorite_notes WHERE noteId = :noteId")
    void deleteFromFavoriteNotes(int noteId);

    @Query("SELECT * FROM notes WHERE id in (select noteId from favorite_notes);")
    LiveData<List<Note>> getFavoriteNotes();

    @Query("INSERT OR IGNORE INTO lock_notes VALUES(:noteId)")
    void insertIntoLockNotes(int noteId);

    @Query("DELETE FROM lock_notes WHERE noteId = :noteId")
    void deleteFromLockNotes(int noteId);

    @Query("SELECT * FROM notes WHERE id in (select noteId from lock_notes);")
    LiveData<List<Note>> getLockNotes();

}
