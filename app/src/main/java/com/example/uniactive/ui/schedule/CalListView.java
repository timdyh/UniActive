package com.example.uniactive.ui.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CalListView extends ListView {
    public CalListView(Context context) {
        super(context);
    }

    public CalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
