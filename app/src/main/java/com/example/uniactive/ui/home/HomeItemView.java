package com.example.uniactive.ui.home;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.uniactive.R;

public class HomeItemView extends RelativeLayout {
    private ImageView iv_icon;      // item的图标
    private TextView tv_item;       // item的文字
    private ImageView iv_bottom;    // item的下划线
    private boolean showBottom;     // 是否显示底部的下划线
    private boolean showIcon;       // 是否显示左侧图标
    
    public HomeItemView(Context context) {
        this(context,null);
    }

    public HomeItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public HomeItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.item_home, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HomeItemView);
        showBottom = ta.getBoolean(R.styleable.HomeItemView_show_bottom, false);
        showIcon = ta.getBoolean(R.styleable.HomeItemView_show_icon, true);

        tv_item = findViewById(R.id.tv_item);
        iv_icon = findViewById(R.id.iv_icon);
        iv_bottom = findViewById(R.id.iv_bottom);

        tv_item.setText(ta.getString(R.styleable.HomeItemView_text));
        iv_icon.setBackgroundResource(ta.getResourceId(R.styleable.HomeItemView_img, R.drawable.setting));
        if (showBottom) {
            iv_bottom.setVisibility(View.VISIBLE);
        }
        if (showIcon) {
            iv_icon.setVisibility(View.VISIBLE);
        }

        ta.recycle();
    }
}
