package com.example.uniactive.ui.schedule;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class CurrentSpan implements LineBackgroundSpan {
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        c.save();
        c.drawColor(Color.parseColor("#ffd6ba"));
        c.restore();
    }
}
