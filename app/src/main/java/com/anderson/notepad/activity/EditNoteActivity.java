package com.anderson.notepad.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.anderson.notepad.R;
import com.anderson.notepad.database.table.Note;
import com.anderson.notepad.databinding.ActivityNoteBinding;
import com.anderson.notepad.edittext.EditTextCache;

public class EditNoteActivity extends AppCompatActivity {

    private ActivityNoteBinding binding;
    private Intent resultIntent = new Intent();
    private Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            note = getIntent().getParcelableExtra("note", Note.class);
        } else {
            note = (Note) getIntent().getParcelableExtra("note");
        }

        resultIntent.putExtra("note", note);

        binding.titleView.setText(note.getTitle());
        binding.descriptionView.setText(note.getDescription());

        EditTextCache editTextCache = new EditTextCache(binding.descriptionView);

        binding.buttonGoBack.setOnClickListener(view -> editTextCache.goLeft());
        binding.buttonGoForward.setOnClickListener(view -> editTextCache.goRight());

        binding.buttonSave.setOnClickListener(view -> {
            note.setTitle(getCurrentTitle());
            note.setDescription(getCurrentDescription());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        PopupMenu popupMenu = createPopupMenu();

        binding.buttonMenu.setOnClickListener(view -> popupMenu.show());

        binding.titleView.setOnFocusChangeListener((View view, boolean hasFocus) -> {
            if (hasFocus) {
                TypedValue value = new TypedValue();
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, value, true);
                binding.titleView.getBackground().setTint(value.data);
            }
        });

    }

    public String getCurrentTitle() {
        return binding.titleView.getText().toString();
    }


    public String getCurrentDescription() {
        return binding.descriptionView.getText().toString();
    }

    private PopupMenu createPopupMenu() {

        PopupMenu popupMenu = new PopupMenu(this, binding.buttonMenu);
        popupMenu.inflate(R.menu.popup_edit_note_activity);

        Menu realPopupMenu = popupMenu.getMenu();

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.item_add_in_favorites_list) {
                item.setVisible(false);
                MenuItem reverseItem = realPopupMenu.findItem(R.id.item_remove_from_favorites_list);
                reverseItem.setVisible(true);

                resultIntent.putExtra("action_favorite_list", "add");

            }

            else if (item.getItemId() == R.id.item_remove_from_favorites_list) {
                item.setVisible(false);
                MenuItem reverseItem = realPopupMenu.findItem(R.id.item_add_in_favorites_list);
                reverseItem.setVisible(true);

                resultIntent.putExtra("action_favorite_list", "remove");

            }

            else if (item.getItemId() == R.id.item_lock_note) {
                AlertDialog dialog = getOnLockAlertDialog();

                if (dialog != null) {
                    dialog.show();

                } else {
                    item.setVisible(false);
                    MenuItem reverseItem = realPopupMenu.findItem(R.id.item_unlock_note);
                    reverseItem.setVisible(true);

                    resultIntent.putExtra("action_lock", "lock");

                }

            }

            else if (item.getItemId() == R.id.item_unlock_note) {
                item.setVisible(false);
                MenuItem reverseItem = realPopupMenu.findItem(R.id.item_lock_note);
                reverseItem.setVisible(true);

                resultIntent.putExtra("action_lock", "unlock");

            }

            else if (item.getItemId() == R.id.item_delete) {
                AlertDialog dialog = getOnDeleteAlertDialog();
                dialog.show();

            }

            return true;

        });

        return popupMenu;

    }

    @NonNull
    private AlertDialog getOnDeleteAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_on_delete);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {

            resultIntent.putExtra("action_delete_note", true);
            setResult(RESULT_OK, resultIntent);
            finish();

        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;
    }

    @NonNull
    public AlertDialog getOnLockAlertDialog() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("note_key", null) != null) {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_on_lock);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            startActivity(new Intent(this, PreferenceActivity.class));
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;

    }

}
