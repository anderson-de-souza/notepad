package com.anderson.notepad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.anderson.notepad.database.RealDatabase;
import com.anderson.notepad.database.table.Note;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private RealDatabase database;

    private LiveData<List<Note>> allNotes;
    private LiveData<List<Note>> favoriteNotes;
    private LiveData<List<Note>> lockNotes;

    public MainViewModel(Application application) {
        super(application);

        database = RealDatabase.getInstance(application.getApplicationContext());

        allNotes = database.noteDAO().getAllNotes();
        favoriteNotes = database.noteDAO().getFavoriteNotes();
        lockNotes = database.noteDAO().getLockNotes();

    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getAllFavoriteNotes() {
        return favoriteNotes;
    }

    public LiveData<List<Note>> getAllLockNotes() {
        return lockNotes;
    }

    public void insertNote(@NonNull Note note) {
        database.noteDAO().insert(note.getTitle(), note.getDescription());
    }

    public void updateNote(@NonNull Note note) {
        database.noteDAO().update(note.getId(), note.getTitle(), note.getDescription());
    }

    public void deleteNote(Note note) {
        database.noteDAO().delete(note);
    }


    public void setAsFavorite(@NonNull Note note) {
        database.noteDAO().insertIntoFavoriteNotes(note.getId());
    }

    public void removeFromFavorite(@NonNull Note note) {
        database.noteDAO().deleteFromFavoriteNotes(note.getId());
    }

    public void lock(@NonNull Note note) {
        database.noteDAO().insertIntoLockNotes(note.getId());
    }

    public void unlock(@NonNull Note note) {
        database.noteDAO().deleteFromLockNotes(note.getId());
    }

    public void deleteAll(int[] ids) {
        database.noteDAO().deleteAll(ids);
    }

}
