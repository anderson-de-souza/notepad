package com.anderson.notepad.edittext;

import android.text.Editable;
import android.text.TextWatcher;

import android.widget.EditText;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EditTextCache implements TextWatcher {
    
    private static final int MAX_SIZE = 32;
    
    private List<String> CACHE = new ArrayList<>();
    private List<CacheTask> ACTIVE_TASKS = new ArrayList<>();
    
    private int cursor;
    private boolean ignore;
    
    private EditText target;
    
    public EditTextCache(@NonNull EditText target) {
    	this.target = target;
        this.target.addTextChangedListener(this);
        CACHE.add(target.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (CACHE.size() > MAX_SIZE) {
            CACHE.remove(0);
        }
    }
    
    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (!ignore) {
            ACTIVE_TASKS.add(new CacheTask(text));
        }
    }
    
    @Override
    public void afterTextChanged(Editable editable) {
        ignore = false;
    }
    
    private int getLastPosition() {
    	return CACHE.size() - 1;
    }
    
    private boolean isLastPosition() {
    	return cursor == getLastPosition();
    }
    
    private void moveToLast() {
        if (!isLastPosition()) {
            cursor = CACHE.size() - 1;
        }
    }
    
    private int moveToLeft() {
        if (cursor > 0) {
            cursor -= 1;
        }
        return cursor;
    }
    
    private int moveToRight() {
    	if (cursor < getLastPosition()) {
            cursor += 1;
        }
        return cursor;
    }
    
    public void goLeft() {
        ignore = true;
        String text = CACHE.get(moveToLeft());
        if (text != null) {
            target.setText(text);
            target.setSelected(false);
            target.clearFocus();
        }
    }
    
    public void goRight() {
        ignore = true;
        String text = CACHE.get(moveToRight());
        if (text != null) {
            target.setText(text);
            target.setSelected(false);
            target.clearFocus();
        }
    }
    
    public List<String> getCache() {
    	return CACHE;
    }
    
    public int getCursor() {
    	return cursor;
    }
    
    public class CacheTask extends Thread {
        
        private CharSequence text;
        
        public CacheTask(CharSequence text) {
            this.text = text;
            start();
        }
    	
        @Override
        public void run() {
            synchronized (EditTextCache.this) {
                
                for (int i = getLastPosition(); i > cursor; --i) {
                    CACHE.remove(i);
                }
                
                CACHE.add(text.toString());
                moveToLast();
                
                ACTIVE_TASKS.remove(this);
                
            }
        }
        
    }
    
}
