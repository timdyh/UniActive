package com.example.uniactive.ui.user;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.uniactive.R;

public class ProfileItemView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_content;
    private EditText et_content;
    private ImageView iv_right;
    private ImageView iv_bottom;
    private boolean showBottom;
    private boolean showRight;
    private boolean editable;

    public ProfileItemView(Context context) {
        this(context,null);
    }

    public ProfileItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ProfileItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.item_profile, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProfileItemView);
        showBottom = ta.getBoolean(R.styleable.ProfileItemView_show_bottom, false);
        showRight = ta.getBoolean(R.styleable.ProfileItemView_show_right, false);
        editable = ta.getBoolean(R.styleable.ProfileItemView_editable, false);

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        et_content = findViewById(R.id.et_content);
        iv_bottom = findViewById(R.id.iv_bottom);
        iv_right = findViewById(R.id.iv_right);

        tv_title.setText(ta.getString(R.styleable.ProfileItemView_title));
        if (showBottom) {
            iv_bottom.setVisibility(View.VISIBLE);
        }
        if (showRight) {
            tv_content.setVisibility(View.VISIBLE);
            iv_right.setVisibility(View.VISIBLE);
        } else {
            et_content.setVisibility(View.VISIBLE);
            if (!editable) {
                et_content.setKeyListener(null);
                et_content.setTextColor(context.getColor(R.color.gray));
            }
        }

        ta.recycle();
    }

    public String getText() {
        if (showRight) {
            return tv_content.getText().toString().trim();
        } else {
            return et_content.getText().toString().trim();
        }
    }

    public void setText(String text) {
        if (showRight) {
            tv_content.setText(text);
        } else {
            et_content.setText(text);
        }
    }
}
