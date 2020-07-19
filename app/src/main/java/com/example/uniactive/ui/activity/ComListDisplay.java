package com.example.uniactive.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ComListDisplay extends AppCompatActivity {

    private List<CommentCard> actCardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private CommentCardAdapter adapter;
    private SharedPreferences sp;
    private int act_id;
    private LinearLayout NoneComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_com_list_display);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        Intent intent = getIntent();
        act_id = intent.getIntExtra("act_id", -1);

        NoneComment = findViewById(R.id.comment_none);

        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.comment_summary);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        JSONGenerator.getActComments(act_id, getApplicationContext(), getListener(), getErrorListener());

    }

    private Response.Listener<JSONObject> getListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        actCardList.clear();
                        JSONArray ja = response.getJSONArray("result");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            String com_name = jo.getString("user");
                            int score = jo.getInt("score");
                            String com_com = jo.getString("comment");
                            String com_time = jo.getString("comment_time");
                            String url = jo.getString("img");
                            actCardList.add(new CommentCard(com_name, score, com_com, com_time, url));
                        }
                        if (actCardList.size() == 0) {
                            NoneComment.setVisibility(View.VISIBLE);
                        }
                        else {
                            NoneComment.setVisibility(View.GONE);
                        }
                        adapter = new CommentCardAdapter(actCardList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ComListDisplay.this, "系统错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ComListDisplay.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        JSONGenerator.cancelAllRequests();
        super.onStop();
    }

}
