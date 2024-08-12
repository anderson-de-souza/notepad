package com.anderson.notepad.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anderson.notepad.MainViewModel;
import com.anderson.notepad.R;
import com.anderson.notepad.adapter.NoteAdapter;
import com.anderson.notepad.database.table.Note;
import com.anderson.notepad.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MainViewModel mainViewModel;

    private NoteAdapter noteAdapter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toobar);

        var drawerToggle = new ActionBarDrawerToggle(
            this, binding.getRoot(), binding.toobar,
            R.string.drawer_is_open, R.string.drawer_is_close
        );

        drawerToggle.setDrawerIndicatorEnabled(true);

        binding.getRoot().addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.listNotes.setHasFixedSize(true);

        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.listNotes.setLayoutManager(layoutManager);

        noteAdapter = new NoteAdapter();

        ActivityResultLauncher<Intent> editNoteLauncher;
        editNoteLauncher = registerForActivityResult(new StartActivityForResult(), activityResult -> {
            if (activityResult.getResultCode() == RESULT_OK) {
                processIntent(activityResult.getData());
            }
        });

        noteAdapter.setOnItemClickListener((view, adapter, position) -> {
            Note clickedNote = noteAdapter.getNoteAt(position);
            Intent editNoteIntent = new Intent(this, EditNoteActivity.class);
            editNoteIntent.putExtra("note", clickedNote);

            if (mainViewModel.getAllLockNotes().getValue().contains(clickedNote)) {
                AlertDialog dialog = getLockNoteClickedAlertDialog(editNoteLauncher, editNoteIntent);
                dialog.show();
            } else {
                editNoteLauncher.launch(editNoteIntent);
            }

        });

        noteAdapter.setOnUpdateSelectedListListener((noteAdapter, selecteds) -> {
            if (actionMode == null) {
                actionMode = startSupportActionMode(actionModeCallback);
            } else {
                actionMode.invalidate();
            }
        });

        binding.listNotes.setAdapter(noteAdapter);

        mainViewModel.getAllNotes().observe(this, allNotes -> {
            if (getNavMenuCheckedItemId() == R.id.item_all_notes) {
                noteAdapter.submitList(allNotes);
                toggleViewForEmptyList(allNotes);
            }
        });

        mainViewModel.getAllFavoriteNotes().observe(this, notes -> {
            if (getNavMenuCheckedItemId() == R.id.item_favorite_notes) {
                noteAdapter.submitList(notes);
                toggleViewForEmptyList(notes);
            }
        });

        mainViewModel.getAllLockNotes().observe(this, notes -> {
            if (getNavMenuCheckedItemId() == R.id.item_lock_notes) {
                noteAdapter.submitList(notes);
                toggleViewForEmptyList(notes);
            }
        });

        ActivityResultLauncher<Intent> addNoteLauncher;
        addNoteLauncher = registerForActivityResult(new StartActivityForResult(), activityResult -> {
            if (activityResult.getResultCode() == RESULT_OK) {
                Note note = getNoteFromParcelable(activityResult.getData());
                mainViewModel.insertNote(note);
            }
        });

        binding.buttonAddNote.setOnClickListener(view ->
            addNoteLauncher.launch(new Intent(this, AddNoteActivity.class))
        );

        binding.navMenu.setCheckedItem(R.id.item_all_notes);

        binding.navMenu.setNavigationItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();

            if (id == R.id.item_settings) {
                startActivity(new Intent(this, PreferenceActivity.class));
                return false;
            }

            List<Note> list = null;

            if (id == R.id.item_all_notes) {
                list = mainViewModel.getAllNotes().getValue();
            } else if (id == R.id.item_favorite_notes) {
                list = mainViewModel.getAllFavoriteNotes().getValue();
            } else if (id == R.id.item_lock_notes) {
                list = mainViewModel.getAllLockNotes().getValue();
            }

            if (
                id == R.id.item_all_notes ||
                id == R.id.item_favorite_notes ||
                id == R.id.item_lock_notes
            ) {
                toggleViewForEmptyList(list);
                noteAdapter.submitList(list);
            }

            binding.getRoot().closeDrawer(GravityCompat.START);
            return true;

        });

    }

    public int getNavMenuCheckedItemId() {
        return binding.navMenu.getCheckedItem().getItemId();
    }

    private void processIntent(@NonNull Intent data) {

        Note note = getNoteFromParcelable(data);

        if (data.hasExtra("action_favorite_list")) {

            switch (data.getStringExtra("action_favorite_list")) {
                case "add":
                    mainViewModel.setAsFavorite(note);
                    break;
                case "remove":
                    mainViewModel.removeFromFavorite(note);
                    break;
            }

        }

        if (data.hasExtra("action_lock")) {

            switch (data.getStringExtra("action_lock")) {
                case "lock":
                    mainViewModel.lock(note);
                    break;
                case "unlock":
                    mainViewModel.unlock(note);
                    break;
            }

        }

        if (data.hasExtra("action_delete_note")) {
            if (data.getBooleanExtra("action_delete_note", false)) {
                mainViewModel.deleteNote(note);
            }
        }

    }

    @NonNull
    private AlertDialog getLockNoteClickedAlertDialog(ActivityResultLauncher<Intent> resultLauncher, Intent target) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));

        EditText editText = new EditText(this);
        ViewGroup.MarginLayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        int marginSize = getResources().getDimensionPixelSize(R.dimen.v24dp);
        params.setMargins(marginSize, 0, marginSize, 0);

        editText.setLayoutParams(params);

        editText.setHint(R.string.note_key);

        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setTitle(R.string.dialog_title_to_lock_note_clicked);

        builder.setPositiveButton(R.string.enter, (dialog, which) -> {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String noteKey = preferences.getString("note_key", null);

            String contentTyped = editText.getText().toString().trim();

            if (noteKey.equals(contentTyped)) {
                resultLauncher.launch(target);
            } else {
                Snackbar.make(binding.buttonAddNote, R.string.message_key_typed_error, Snackbar.LENGTH_SHORT).show();
            }

        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);

        return builder.create();

    }

    private void toggleViewForEmptyList(@NonNull List<Note> list) {
        if (list.isEmpty()) {
            showViewForEmptyList();
        } else if (binding.viewForEmptyList.getVisibility() == View.VISIBLE) {
            dismissViewForEmptyList();
        }
    }

    private void showViewForEmptyList() {

        if (binding.viewForEmptyList.getVisibility() == View.VISIBLE) {
            binding.viewForEmptyList.setVisibility(View.GONE);
        }

        binding.listNotes.setVisibility(View.GONE);
        binding.viewLoading.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            binding.viewLoading.setVisibility(View.GONE);
            binding.viewForEmptyList.setVisibility(View.VISIBLE);
        }, 800);

    }

    private void dismissViewForEmptyList() {

        if (binding.listNotes.getVisibility() == View.VISIBLE) {
            binding.listNotes.setVisibility(View.GONE);
        }

        binding.viewForEmptyList.setVisibility(View.GONE);
        binding.viewLoading.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            binding.viewLoading.setVisibility(View.GONE);
            binding.listNotes.setVisibility(View.VISIBLE);
        }, 500);

    }

    public Note getNoteFromParcelable(Intent data) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return data.getParcelableExtra("note", Note.class);
        } else {
            return data.getParcelableExtra("note");
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(@NonNull ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.main_activity_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(@NonNull ActionMode mode, @NonNull Menu menu) {

            String length = String.valueOf(noteAdapter.getSelectedNotes().size());
            mode.setTitle(getString(R.string.x_items_selecteds, length));

            menu.findItem(R.id.item_deselect_all_list_items).setVisible(noteAdapter.isEveryNoteSelected());
            menu.findItem(R.id.item_select_all_list_items).setVisible(!noteAdapter.isEveryNoteSelected());

            if (noteAdapter.getSelectedNotes().isEmpty()) {
                mode.finish();
            }

            return true;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NonNull MenuItem item) {

            if (item.getItemId() == R.id.item_select_all_list_items) {
                noteAdapter.selectAll();
                mode.invalidate();

            } else if (item.getItemId() == R.id.item_deselect_all_list_items) {
                noteAdapter.deselectAll();
                mode.invalidate();

            } else if (item.getItemId() == R.id.item_delete_all_selected_list_items) {

                List<Note> selectedNotes = noteAdapter.getSelectedNotes();

                int size = selectedNotes.size();
                int[] noteIds = new int[size];

                for (int i = 0; i < size; i++) {
                    noteIds[i] = selectedNotes.get(i).getId();
                }

                noteAdapter.exitSelectionMode();
                mainViewModel.deleteAll(noteIds);
                mode.finish();

            }

            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            noteAdapter.exitSelectionMode();
            actionMode = null;
        }

    };

}