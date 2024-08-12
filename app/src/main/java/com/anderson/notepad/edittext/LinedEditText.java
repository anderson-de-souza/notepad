package com.anderson.notepad.edittext;

import static android.content.res.ColorStateList.valueOf;
import static com.google.android.material.R.attr.colorOnPrimaryContainer;

import android.content.Context;

import android.content.res.Resources;
import android.content.res.TypedArray;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.text.InputType;

import android.util.AttributeSet;
import android.util.TypedValue;

import android.view.Gravity;

import android.widget.EditText;

import com.anderson.notepad.R;

public class LinedEditText extends EditText {
    
    private Paint linePaint;
    private Rect lineData;
    
    private boolean lined = true;
    
    public LinedEditText(Context context) {
    	super(context);
        initialize(context, null);
    }
    
    public LinedEditText(Context context, AttributeSet attr) {
    	super(context, attr);
        initialize(context, attr);
    }
    
    public LinedEditText(Context context, AttributeSet attr, int defStyle) {
    	super(context, attr, defStyle);
        initialize(context, attr);
    }
    
    private void initialize(Context context, AttributeSet attr) {
        
        TypedValue value = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(colorOnPrimaryContainer, value, false);
        
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(value.data);
        
        lineData = new Rect();
        
        setBackgroundTintList(valueOf(Color.TRANSPARENT));
        
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        setGravity(Gravity.TOP | Gravity.START | Gravity.LEFT);
        
        TypedArray data = context.obtainStyledAttributes(attr, R.styleable.LinedEditText);
        lined = data.getBoolean(R.styleable.LinedEditText_lined, true);
        
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (lined) {
            
            int height = getHeight();
            int lineHeight = getLineHeight();
        
            int count = height / lineHeight;
        
            if (getLineCount() > count) {
                count = getLineCount();
            }
            
            int baseline = getLineBounds(0, lineData);
            
            for (int i = 0; i < count; i++) {
                canvas.drawLine(lineData.left, baseline + 2, lineData.right, baseline + 2, linePaint);
                baseline += getLineHeight();
                baseline -= 2;
            }
            
        }
        
    }
    
}
