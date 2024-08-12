package com.anderson.notepad.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.anderson.notepad.database.table.Note;
import com.anderson.notepad.databinding.ActivityNoteBinding;
import com.anderson.notepad.edittext.EditTextCache;

import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    private ActivityNoteBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        var editTextCache = new EditTextCache(binding.descriptionView);

        binding.buttonGoBack.setOnClickListener(view -> editTextCache.goLeft());
        binding.buttonGoForward.setOnClickListener(view -> editTextCache.goRight());

        Note note = new Note();

        binding.titleView.setText(note.getTitle());
        binding.descriptionView.setText(note.getDescription());

        binding.buttonSave.setOnClickListener(view -> {
            note.setTitle(binding.titleView.getText().toString());
            note.setDescription(binding.descriptionView.getText().toString());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("note", note);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        binding.buttonMenu.setVisibility(View.GONE);

        binding.titleView.setOnFocusChangeListener((View view, boolean hasFocus) -> {
            if (hasFocus) {
                TypedValue value = new TypedValue();
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, value, true);
                binding.titleView.getBackground().setTint(value.data);
            }
        });

    }
}
