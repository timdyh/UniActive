package com.example.uniactive.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.uniactive.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ShowCommentActivity extends AppCompatActivity {
    private TextView show_comments;
    private RatingBar show_ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_show);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        show_comments = findViewById(R.id.show_comments);
        show_ratingBar = findViewById(R.id.show_ratingBar);

        Intent intent = getIntent();
        show_comments.setText(intent.getStringExtra("comment"));
        int score = intent.getIntExtra("score", 0);
        show_ratingBar.setRating(score);
    }
}
