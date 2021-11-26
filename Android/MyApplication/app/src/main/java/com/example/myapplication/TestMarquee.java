package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class TestMarquee extends TextView {

    public TestMarquee(Context context) {
        super(context);
    }

    public TestMarquee(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestMarquee(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
